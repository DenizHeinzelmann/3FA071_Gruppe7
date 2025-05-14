package utils;

import controller.AnalysisHandler;
import controller.CustomerHandler;
import controller.LoginHandler;
import controller.ReadingHandler;
import controller.RegisterHandler;
import filter.CorsFilter;
import repository.CustomerRepository;
import repository.DatabaseConnection;
import repository.ReadingRepository;
import repository.UserRepository;
import com.sun.net.httpserver.HttpServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Properties;

public class Server {
    private static HttpServer server;

    public static void startServer(String url) throws IOException {
        String[] parts = url.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("URL muss im Format http://host:port sein");
        }
        String host = parts[1].replace("//", "");
        int port;
        try {
            port = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port muss eine gültige Zahl sein.", e);
        }

        Properties properties = new Properties();
        try (InputStream inStream = Server.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (inStream == null) {
                throw new FileNotFoundException("Die Datei 'database.properties' wurde im Klassenpfad nicht gefunden.");
            }
            properties.load(inStream);
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der database.properties: " + e.getMessage());
            throw new IOException("Datenbank-Konfiguration fehlgeschlagen.", e);
        }

        server = HttpServer.create(new InetSocketAddress(host, port), 0);

        try {
            DatabaseConnection.getInstance().openConnection(properties);
            CustomerRepository customerRepository = new CustomerRepository();
            ReadingRepository readingRepository = new ReadingRepository();
            UserRepository userRepository = new UserRepository();

            // Endpoint für grafische Auswertung (Analysis) VOR dem allgemeinen Readings-Handler
            server.createContext(
                    "/api/readings/analysis",
                    new CorsFilter(new AnalysisHandler(readingRepository))
            );

            // Bestehende Endpunkte
            server.createContext(
                    "/api/customers",
                    new CorsFilter(new CustomerHandler(customerRepository))
            );
            server.createContext(
                    "/api/readings",
                    new CorsFilter(new ReadingHandler(readingRepository, customerRepository))
            );

            // Endpoints für Userverwaltung
            server.createContext(
                    "/api/users/login",
                    new CorsFilter(new LoginHandler(userRepository))
            );
            server.createContext(
                    "/api/users/register",
                    new CorsFilter(new RegisterHandler(userRepository))
            );

            server.setExecutor(null); // Default-Executor
            server.start();
            System.out.println("Server gestartet auf " + url);
        } catch (SQLException e) {
            System.err.println("Fehler beim Initialisieren der Repositories: " + e.getMessage());
            server.stop(0);
            throw new IOException("Server konnte nicht gestartet werden aufgrund eines Datenbankfehlers.", e);
        }
    }

    public static void stopServer() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server gestoppt.");
        } else {
            System.out.println("Server war nicht gestartet.");
        }
    }
}
