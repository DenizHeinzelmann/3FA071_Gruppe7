// src/main/java/controller/ReadingHandler.java

package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Reading;
import repository.ReadingRepository;
import repository.CustomerRepository;
import utils.JsonUtil;

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;

public class ReadingHandler implements HttpHandler {
    private final ReadingRepository readingRepository;
    private final CustomerRepository customerRepository;

    public ReadingHandler(ReadingRepository readingRepository, CustomerRepository customerRepository) {
        this.readingRepository = readingRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            URI uri = exchange.getRequestURI();
            String path = uri.getPath();
            String[] pathParts = path.split("/");

            if (method.equalsIgnoreCase("GET")) {
                if (pathParts.length == 4) { // /api/readings/{id}
                    handleGetReading(exchange, pathParts[3]);
                } else if (pathParts.length == 6 && "customer".equals(pathParts[4])) { // /api/readings/customer/{customerId}
                    handleGetReadingsByCustomer(exchange, pathParts[5]);
                } else { // /api/readings
                    handleGetAllReadings(exchange);
                }
            } else if (method.equalsIgnoreCase("POST")) {
                handleCreateReading(exchange);
            } else if (method.equalsIgnoreCase("PUT")) {
                if (pathParts.length == 4) { // /api/readings/{id}
                    handleUpdateReading(exchange, pathParts[3]);
                } else {
                    sendResponse(exchange, 400, "Invalid URL for PUT");
                }
            } else if (method.equalsIgnoreCase("DELETE")) {
                if (pathParts.length == 4) { // /api/readings/{id}
                    handleDeleteReading(exchange, pathParts[3]);
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

    private void handleGetReading(HttpExchange exchange, String idStr) throws IOException {
        try {
            UUID id = UUID.fromString(idStr);
            Reading reading = readingRepository.getReading(id);
            if (reading != null) {
                String response = JsonUtil.toJson(reading);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 404, "Reading not found");
            }
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Invalid UUID format");
        } catch (Exception e) {
            e.printStackTrace(); // Stacktrace ausgeben
            sendResponse(exchange, 500, "Error retrieving reading (HandlerClass)");
        }
    }

    private void handleGetAllReadings(HttpExchange exchange) throws IOException {
        try {
            List<Reading> readings = readingRepository.getAllReadings();
            String response = JsonUtil.toJson(readings);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace(); // Stacktrace ausgeben
            sendResponse(exchange, 500, "Error retrieving readings");
        }
    }

    private void handleGetReadingsByCustomer(HttpExchange exchange, String customerIdStr) throws IOException {
        try {
            UUID customerId = UUID.fromString(customerIdStr);
            List<Reading> readings = readingRepository.getReadingsByCustomer(customerId);
            String response = JsonUtil.toJson(readings);
            sendResponse(exchange, 200, response);
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Invalid UUID format for customerId");
        } catch (Exception e) {
            sendResponse(exchange, 500, "Error retrieving readings by customer");
        }
    }

    private void handleCreateReading(HttpExchange exchange) throws IOException {
        String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().reduce("", (acc, line) -> acc + line);
        try {
            Reading reading = JsonUtil.fromJson(body, Reading.class);

            // VALIDIERUNG
            if (reading.getMeterId() == null || reading.getMeterId().trim().isEmpty()
                    || reading.getKindOfMeter() == null
                    || reading.getDateOfReading() == null
                    || reading.getMeterCount() <= 0) {
                sendResponse(exchange, 400, "Pflichtfelder fehlen: meterId, meterCount (>0), kindOfMeter, dateOfReading.");
                return;
            }

            // Optional: Prüfe auf gültigen Kunden, falls vorhanden
            if (reading.getCustomer() != null) {
                UUID customerId = reading.getCustomer().getid();
                if (customerId == null || customerRepository.getCustomer(customerId) == null) {
                    UUID newCustomerId = customerRepository.createCustomer(reading.getCustomer());
                    reading.getCustomer().setid(newCustomerId);
                } else {
                    reading.setCustomer(customerRepository.getCustomer(customerId));
                }
            }

            UUID id = readingRepository.createReading(reading);
            reading.setid(id);
            String response = JsonUtil.toJson(reading);
            sendResponse(exchange, 201, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "Invalid JSON format or data: " + e.getMessage());
        }
    }



    private void handleUpdateReading(HttpExchange exchange, String idStr) throws IOException {
        try {
            UUID id = UUID.fromString(idStr);
            InputStream is = exchange.getRequestBody();
            String body = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .reduce("", (acc, line) -> acc + line);
            Reading reading = JsonUtil.fromJson(body, Reading.class);
            readingRepository.updateReading(id, reading);
            sendResponse(exchange, 200, "Reading updated successfully");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Invalid UUID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "Invalid JSON format or data: " + e.getMessage());
        }
    }

    private void handleDeleteReading(HttpExchange exchange, String idStr) throws IOException {
        try {
            UUID id = UUID.fromString(idStr);
            readingRepository.deleteReading(id);
            sendResponse(exchange, 200, "Reading deleted successfully");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Invalid UUID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Error deleting reading");
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
