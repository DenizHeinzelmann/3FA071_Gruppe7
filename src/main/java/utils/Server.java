package utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import interfaces.Route;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;


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
            // Check if the method has the @interfaces.Route annotation
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

                    // Send the response
                    String responseString = response != null ? response.toString() : "OK";
                    exchange.sendResponseHeaders(200, responseString.getBytes().length);
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

}
