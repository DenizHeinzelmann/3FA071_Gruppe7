package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.JwtUtil;
import utils.JsonUtil;
import model.User;
import repository.UserRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class LoginHandler implements HttpHandler {

    private final UserRepository userRepository;

    public LoginHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            InputStream is = exchange.getRequestBody();
            String body = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .reduce("", (acc, line) -> acc + line);
            try {
                // Erwartet JSON: { "username": "demo", "password": "demo" }
                LoginRequest loginRequest = JsonUtil.fromJson(body, LoginRequest.class);
                User user = userRepository.getUserByUsername(loginRequest.getUsername());

                // Dummy-Prüfung: In einer echten Anwendung sollten Sie das Passwort gehasht vergleichen!
                if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
                    String token = JwtUtil.generateToken(user.getId().toString());
                    LoginResponse responseObj = new LoginResponse(token);
                    String responseJson = JsonUtil.toJson(responseObj);
                    sendResponse(exchange, 200, responseJson);
                } else {
                    sendResponse(exchange, 401, "{\"error\":\"Invalid credentials\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 400, "{\"error\":\"Bad Request\"}");
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes("UTF-8");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    // Innere Klassen für Request/Response
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;

        public LoginResponse(String token) { this.token = token; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
