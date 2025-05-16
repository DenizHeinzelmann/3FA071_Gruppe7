package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.JsonUtil;
import model.User;
import repository.UserRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class RegisterHandler implements HttpHandler {

    private final UserRepository userRepository;

    public RegisterHandler(UserRepository userRepository) {
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
                User user = JsonUtil.fromJson(body, User.class);
                userRepository.createUser(user);
                String responseJson = JsonUtil.toJson(user);
                sendResponse(exchange, 201, responseJson);
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
}
