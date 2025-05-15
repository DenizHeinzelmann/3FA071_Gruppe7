package controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.CustomerRepository;
import utils.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class CustomerHandlerTest {

    private CustomerRepository customerRepository;
    private CustomerHandler handler;
    private HttpExchange exchange;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        handler = new CustomerHandler(customerRepository);
        exchange = mock(HttpExchange.class);
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
    }

    @Test
    void testHandleGetAllCustomers() throws Exception {
        URI uri = new URI("/api/customers");
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(uri);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        when(customerRepository.getAllCustomers()).thenReturn(List.of(
                new Customer(UUID.randomUUID(), "Anna", "Muster", LocalDate.of(1990, 1, 1), null)
        ));

        handler.handle(exchange);
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }

    @Test
    void testHandleGetSingleCustomer() throws Exception {
        UUID id = UUID.randomUUID();
        URI uri = new URI("/api/customers/" + id);
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(uri);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        when(customerRepository.getCustomer(id)).thenReturn(
                new Customer(id, "Max", "Test", null, null)
        );

        handler.handle(exchange);
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }
}
