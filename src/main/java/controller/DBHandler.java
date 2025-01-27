package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import repository.DatabaseConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.sql.SQLException;


public class DBHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();

            if (method.equalsIgnoreCase("DELETE")) {
                DatabaseConnection.getInstance().removeAllTables();
                DatabaseConnection.getInstance().createAllTables();
                sendResponse(exchange, 200,"Removed and recreated tables");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
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

