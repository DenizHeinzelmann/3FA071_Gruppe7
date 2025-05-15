package controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import model.AnalysisData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ReadingRepository;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalysisHandlerTest {

    private ReadingRepository readingRepository;
    private AnalysisHandler handler;
    private HttpExchange exchange;

    @BeforeEach
    void setUp() {
        readingRepository = mock(ReadingRepository.class);
        handler = new AnalysisHandler(readingRepository);
        exchange = mock(HttpExchange.class);
    }

    @Test
    void testHandleValidGetRequest() throws Exception {
        URI uri = new URI("/api/readings/analysis?period=1");
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(uri);
        when(exchange.getResponseHeaders()).thenReturn(new Headers()); // FIX
        OutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);

        when(readingRepository.getAnalysisData(1)).thenReturn(List.of(
                new AnalysisData("STROM", "2024-01", 123.45)
        ));

        handler.handle(exchange);
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }

    @Test
    void testHandleInvalidPeriodParameter() throws Exception {
        URI uri = new URI("/api/readings/analysis?period=abc");
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(uri);
        when(exchange.getResponseHeaders()).thenReturn(new Headers()); // FIX
        OutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);

        handler.handle(exchange);
        verify(exchange).sendResponseHeaders(eq(400), anyLong());
    }
}
