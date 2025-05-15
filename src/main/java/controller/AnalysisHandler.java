package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import repository.ReadingRepository;
import utils.JsonUtil;
import model.AnalysisData;

import java.io.*;
import java.net.URI;
import java.util.List;

public class AnalysisHandler implements HttpHandler {
    private final ReadingRepository readingRepository;

    public AnalysisHandler(ReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            URI requestURI = exchange.getRequestURI();
            String query = requestURI.getQuery();
            int periodYears = 1; // Default
            if (query != null && query.contains("period=")) {
                try {
                    periodYears = Integer.parseInt(query.split("period=")[1]);
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "{\"error\":\"Invalid period parameter\"}");
                    return;
                }
            }
            try {
                List<AnalysisData> analysisData = readingRepository.getAnalysisData(periodYears);
                String response = JsonUtil.toJson(analysisData);
                sendResponse(exchange, 200, response);
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\":\"Error retrieving analysis data\"}");
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes("UTF-8");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
