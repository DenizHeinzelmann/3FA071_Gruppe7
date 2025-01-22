package utils;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.net.URI;


public class Server {
    private static HttpServer server;

    public static void startServer(String url) throws Exception {
        ResourceConfig config = new ResourceConfig();
        config.packages("api");
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(url), config);
        server.start();
    }
    public static void stopServer(){
        server.shutdown();
    }
}


