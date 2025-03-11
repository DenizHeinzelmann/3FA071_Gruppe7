// src/main/java/controller/CustomerHandler.java
package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Customer;
import repository.CustomerRepository;
import utils.JsonUtil;

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;

public class CustomerHandler implements HttpHandler {
    private final CustomerRepository customerRepository;

    public CustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Setze CORS-Header
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String[] pathParts = path.split("/");

        // Behandle OPTIONS-Anfragen für CORS
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1); // No Content
            return;
        }

        try {
            if (method.equalsIgnoreCase("GET")) {
                if (pathParts.length == 4) { // /api/customers/{id}
                    handleGetCustomer(exchange, pathParts[3]);
                } else { // /api/customers
                    handleGetAllCustomers(exchange);
                }
            } else if (method.equalsIgnoreCase("POST")) {
                handleCreateCustomer(exchange);
            } else if (method.equalsIgnoreCase("PUT")) {
                if (pathParts.length == 4) { // /api/customers/{id}
                    handleUpdateCustomer(exchange, pathParts[3]);
                } else {
                    sendResponse(exchange, 400, "Invalid URL for PUT");
                }
            } else if (method.equalsIgnoreCase("DELETE")) {
                if (pathParts.length == 4) { // /api/customers/{id}
                    handleDeleteCustomer(exchange, pathParts[3]);
                } else {
                    sendResponse(exchange, 400, "Invalid URL for DELETE");
                }
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void handleGetCustomer(HttpExchange exchange, String idStr) throws IOException {
        try {
            UUID id = UUID.fromString(idStr);
            Customer customer = customerRepository.getCustomer(id);
            if (customer != null) {
                String response = JsonUtil.toJson(customer);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 404, "Customer not found");
            }
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Invalid UUID format");
        } catch (Exception e) {
            sendResponse(exchange, 500, "Error retrieving customer");
        }
    }

    private void handleGetAllCustomers(HttpExchange exchange) throws IOException {
        try {
            List<Customer> customers = customerRepository.getAllCustomers();
            String response = JsonUtil.toJson(customers);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "Error retrieving customers");
        }
    }

    private void handleCreateCustomer(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new BufferedReader(new InputStreamReader(is))
                .lines()
                .reduce("", (acc, line) -> acc + line);
        try {
            Customer customer = JsonUtil.fromJson(body, Customer.class);
            UUID id = customerRepository.createCustomer(customer);
            customer.setid(id); // Stelle sicher, dass die Methode korrekt ist
            String response = JsonUtil.toJson(customer);
            sendResponse(exchange, 201, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "Invalid JSON format or data: " + e.getMessage());
        }
    }

    private void handleUpdateCustomer(HttpExchange exchange, String idStr) throws IOException {
        try {
            UUID id = UUID.fromString(idStr);
            InputStream is = exchange.getRequestBody();
            String body = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .reduce("", (acc, line) -> acc + line);
            Customer customer = JsonUtil.fromJson(body, Customer.class);
            customerRepository.updateCustomer(id, customer);
            sendResponse(exchange, 200, "Customer updated successfully");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Invalid UUID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "Invalid JSON format or data: " + e.getMessage());
        }
    }

    private void handleDeleteCustomer(HttpExchange exchange, String idStr) throws IOException {
        try {
            UUID id = UUID.fromString(idStr);
            customerRepository.deleteCustomer(id);
            sendResponse(exchange, 200, "Customer deleted successfully");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Invalid UUID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Error deleting customer");
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
