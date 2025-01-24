package utils;

import api.CustomerResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import repository.DatabaseConnection;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;


public class Server {
    private static HttpServer server;

    public static void startServer(String url) throws Exception {

        final ResourceConfig config = new ResourceConfig().packages("api");
       // final ResourceConfig config = new ResourceConfig(CustomerResource.class);

        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(url), config);
        server.start();
    }
    public static void stopServer(){
        server.shutdown();
    }

// for testing purposes
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (InputStream input = Server.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new FileNotFoundException("The file 'database.properties' was not found!");
            }
            properties.load(input);
        }

        DatabaseConnection.getInstance().openConnection(properties);
        Server.startServer("http://localhost:8080");
    }
}


