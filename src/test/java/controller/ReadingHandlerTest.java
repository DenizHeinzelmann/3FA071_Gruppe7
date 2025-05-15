package controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import model.Customer;
import model.Reading;
import repository.CustomerRepository;
import repository.ReadingRepository;
import utils.JsonUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ReadingHandlerTest {

    private ReadingRepository readingRepo;
    private CustomerRepository customerRepo;
    private ReadingHandler handler;
    private HttpExchange exchange;

    @BeforeEach
    void setUp() {
        readingRepo = mock(ReadingRepository.class);
        customerRepo = mock(CustomerRepository.class);
        handler = new ReadingHandler(readingRepo, customerRepo);
        exchange = mock(HttpExchange.class);
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
    }

    @Test
    void testGetAllReadings() throws Exception {
        URI uri = new URI("/api/readings");
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(uri);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        when(readingRepo.getAllReadings()).thenReturn(List.of());

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }
}
