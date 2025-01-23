// src/main/java/utils/Server.java

package utils;

import controller.CustomerHandler;
import controller.ReadingHandler;
import repository.CustomerRepository;
import repository.DatabaseConnection;
import repository.ReadingRepository;
import com.sun.net.httpserver.HttpServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Properties;

public class Server {
    private static HttpServer server;

    /**
     * Startet den REST-Server.
     *
     * @param url Die URL inklusive Port, z.B. "http://localhost:8000"
     * @throws IOException Wenn der Server nicht gestartet werden kann
     */
    public static void startServer(String url) throws IOException {
        // Extrahiere Host und Port aus der URL
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

        // Lade die 'database.properties' Datei
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

        // Starte den HTTP-Server
        server = HttpServer.create(new InetSocketAddress(host, port), 0);

        try {
            // Öffne die Datenbankverbindung
            DatabaseConnection.getInstance().openConnection(properties);

            // Initialisiere Repositories
            CustomerRepository customerRepository = new CustomerRepository();
            ReadingRepository readingRepository = new ReadingRepository();

            // Erstelle Kontexte für REST-Endpunkte
            server.createContext("/api/customers", new CustomerHandler(customerRepository));
            server.createContext("/api/readings", new ReadingHandler(readingRepository, customerRepository));

            server.setExecutor(null); // Default-Executor
            server.start();
            System.out.println("Server gestartet auf " + url);
        } catch (SQLException e) {
            System.err.println("Fehler beim Initialisieren der Repositories: " + e.getMessage());
            // Stoppe den Server, wenn die Repositories nicht initialisiert werden können
            server.stop(0);
            throw new IOException("Server konnte nicht gestartet werden aufgrund eines Datenbankfehlers.", e);
        }
    }

    /**
     * Stoppt den REST-Server.
     */
    public static void stopServer() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server gestoppt.");
        } else {
            System.out.println("Server war nicht gestartet.");
        }
    }
}