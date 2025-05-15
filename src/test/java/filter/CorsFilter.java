package filter;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;

import static org.mockito.Mockito.*;

class CorsFilterTest {

    private CorsFilter corsFilter;
    private HttpExchange exchange;
    private HttpHandler nextHandler;

    @BeforeEach
    void setUp() {
        nextHandler = mock(HttpHandler.class);
        corsFilter = new CorsFilter(nextHandler);
        exchange = mock(HttpExchange.class);

        Headers headers = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(headers);
    }

    @Test
    void testCorsOptionsRequest() throws Exception {
        when(exchange.getRequestMethod()).thenReturn("OPTIONS");

        corsFilter.handle(exchange);

        verify(exchange).sendResponseHeaders(204, -1);
        verify(nextHandler, never()).handle(any());
    }

    @Test
    void testCorsNormalRequest() throws Exception {
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getResponseBody()).thenReturn(mock(OutputStream.class));

        corsFilter.handle(exchange);

        verify(nextHandler).handle(exchange);
    }
}
