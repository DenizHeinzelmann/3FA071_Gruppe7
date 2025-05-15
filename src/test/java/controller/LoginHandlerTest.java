package controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.UserRepository;
import utils.JsonUtil;
import utils.JwtUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import static org.mockito.Mockito.*;

class LoginHandlerTest {

    private UserRepository userRepository;
    private LoginHandler handler;
    private HttpExchange exchange;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        handler = new LoginHandler(userRepository);
        exchange = mock(HttpExchange.class);
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        String username = "demo";
        String password = "demo";
        UUID userId = UUID.randomUUID();

        User user = new User(username, password, "ADMIN");

        // Simuliere Anfrage-Body
        String requestJson = "{\"username\":\"demo\",\"password\":\"demo\"}";
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(requestJson.getBytes()));
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
        when(userRepository.getUserByUsername("demo")).thenReturn(user);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }

    @Test
    void testInvalidCredentials() throws Exception {
        String requestJson = "{\"username\":\"wrong\",\"password\":\"wrong\"}";
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(requestJson.getBytes()));
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
        when(userRepository.getUserByUsername("wrong")).thenReturn(null); // Kein Benutzer

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(401), anyLong());
    }

    @Test
    void testMalformedJson() throws Exception {
        String brokenJson = "{\"username\": \"x\", "; // absichtlich kaputt
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(brokenJson.getBytes()));
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(400), anyLong());
    }

    @Test
    void testWrongMethod() throws Exception {
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(405), anyLong());
    }
}
