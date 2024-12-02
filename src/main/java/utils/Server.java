package utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import interfaces.Route;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;

public class Server {

    public static void startServer(Server serverClass) throws Exception {
        // Create HTTP server listening on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Dynamically register the routes using reflection
        registerRoutes(server, serverClass);

        // Start the server
        server.setExecutor(null); // uses default executor
        server.start();
        System.out.println("utils.Server started on http://localhost:8000");
    }

    public static void registerRoutes(HttpServer server, Object handlerInstance) {
        // Get all methods from the handler class
        Method[] methods = handlerInstance.getClass().getDeclaredMethods();

        for (Method method : methods) {
            // Check if the method has the @Route annotation
            if (method.isAnnotationPresent(Route.class)) {
                Route routeAnnotation = method.getAnnotation(Route.class);

                // Create the handler for this route
                HttpHandler handler = createHandler(handlerInstance, method, routeAnnotation);

                // Register the route with the server (using the path from annotation)
                server.createContext(routeAnnotation.path(), handler);
                System.out.println("Registered route: " + Arrays.toString(routeAnnotation.method()) + " for " + routeAnnotation.path());
            }
        }
    }

    private static HttpHandler createHandler(Object handlerInstance, Method method, Route routeAnnotation) {
        return exchange -> {
            // Check if the HTTP method matches one of the allowed methods from the annotation
            String requestMethod = exchange.getRequestMethod();
            if (Arrays.asList(routeAnnotation.method()).contains(requestMethod)) {
                try {
                    // Call the annotated method dynamically using reflection
                    Object response = method.invoke(handlerInstance, exchange);

                    // Get the custom response status if any, else use 200 OK
                    int statusCode = getResponseStatusCode(response);

                    // Send the response with the appropriate status code
                    String responseString = response != null ? response.toString() : "OK";
                    exchange.sendResponseHeaders(statusCode, responseString.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(responseString.getBytes());
                    os.close();
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);  // Method Not Allowed
            }
        };
    }

    private static int getResponseStatusCode(Object response) {
        if (response instanceof ResponseWrapper) {
            return ((ResponseWrapper) response).getStatusCode();
        }
        return 200;
    }

    protected static String getRequestMethod(HttpExchange exchange){
        return exchange.getRequestMethod();
    }

    // Helper method to read the request body as a String
    protected static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream requestBodyStream = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(requestBodyStream));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        return requestBody.toString();
    }

    // Response wrapper to encapsulate both response data and status code
    public static class ResponseWrapper {
        private final String responseData;
        private final int statusCode;

        public ResponseWrapper(String responseData, int statusCode) {
            this.responseData = responseData;
            this.statusCode = statusCode;
        }

        public String getResponseData() {
            return responseData;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
    public JSONObject readJsonRequest(String response) {
        return new JSONObject(response);
    }

    public JSONObject createJsonResponse(boolean status, String message) {
        JSONObject responseJson = new JSONObject();

        responseJson.put("success", status);
        responseJson.put("message", message);

        return responseJson;
    }

    public <T> JSONObject createJsonResponse(boolean status, String message, Map<String, T> data) {
        JSONObject responseJson = new JSONObject();
        if (status) {
            responseJson.put("status", "success");
            responseJson.put("message", message);
            responseJson.put("data", data);
        }
        return responseJson;
    }
}
