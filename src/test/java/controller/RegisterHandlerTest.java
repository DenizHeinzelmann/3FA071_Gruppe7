package controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.UserRepository;
import utils.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.mockito.Mockito.*;

class RegisterHandlerTest {

    private UserRepository userRepository;
    private RegisterHandler handler;
    private HttpExchange exchange;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        handler = new RegisterHandler(userRepository);
        exchange = mock(HttpExchange.class);
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
    }

    @Test
    void testSuccessfulRegistration() throws Exception {
        User user = new User("demo", "pass", "ADMIN");
        String json = JsonUtil.toJson(user);

        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(json.getBytes()));
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(userRepository).createUser(any());
        verify(exchange).sendResponseHeaders(eq(201), anyLong());
    }

    @Test
    void testMalformedJson() throws Exception {
        String broken = "{\"username\": \"x\""; // Invalid JSON
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(broken.getBytes()));
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
