package repository;

import interfaces.IDatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection implements IDatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public Connection openConnection(Properties properties) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            System.out.println("Verbindung bereits geöffnet.");
            return connection;
        }

        System.out.println("Aktueller Benutzer: " + System.getProperty("user.name"));
        String userName = System.getProperty("user.name");
        System.out.println("Aktueller Benutzer: " + userName);
        System.out.println("Properties Keys:");
        properties.forEach((key, value) -> System.out.println(key + ": " + value));

        String url = properties.getProperty(userName + ".db.url");
        String user = properties.getProperty(userName + ".db.user");
        String password = properties.getProperty(userName + ".db.pw");

        System.out.println("Geladene Properties:");
        System.out.println("URL: " + url);
        System.out.println("Benutzer: " + user);
        System.out.println("Passwort: " + (password != null ? "****" : "Nicht gesetzt"));

        if (url == null || user == null || password == null) {
            throw new IllegalArgumentException("Fehlende Datenbankverbindungsinformationen in den Properties.");
        }

        try {
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(true); // Auto-Commit aktivieren
            System.out.println("Connected to database server successfully!");

            String dbName = url.substring(url.lastIndexOf("/") + 1);
            System.out.println("Datenbankname: " + dbName);

            createDatabase(dbName);
            connection.setCatalog(dbName);
            System.out.println("Using database: " + dbName);

            createAllTables();
        } catch (SQLException e) {
            System.err.println("Fehler beim Herstellen der Verbindung: " + e.getMessage());
            throw e;
        }

        return connection;
    }

    private void createDatabase(String dbName) throws SQLException {
        String sqlCreateDatabase = """
                CREATE DATABASE IF NOT EXISTS %s
                DEFAULT CHARACTER SET utf8mb4
                COLLATE utf8mb4_unicode_ci;
                """.formatted(dbName);

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sqlCreateDatabase);
            System.out.println("Database created or already exists: " + dbName);
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Datenbank: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void createAllTables() throws SQLException {
        String sqlCreateCustomers = """
                CREATE TABLE IF NOT EXISTS customers (
                    id CHAR(36) PRIMARY KEY, -- UUID als CHAR(36)
                    firstname VARCHAR(255),
                    lastname VARCHAR(255),
                    birthdate DATE,
                    gender ENUM('M', 'W', 'D') -- Enums für Geschlechter
                );
                """;

        String sqlCreateReadings = """
                CREATE TABLE IF NOT EXISTS readings (
                    id CHAR(36) PRIMARY KEY, -- UUID als CHAR(36)
                    customer_id CHAR(36),
                    kind_of_meter ENUM('WASSER', 'STROM', 'HEIZUNG'), -- Enums für Zählerarten
                    meter_count DECIMAL(10,2),
                    date_of_reading DATE,
                    meter_id VARCHAR(255),
                    substitute BOOLEAN,
                    comment TEXT,
                    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL
                );
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlCreateCustomers);
            System.out.println("Kundentabelle erfolgreich erstellt oder existiert bereits.");

            stmt.execute(sqlCreateReadings);
            System.out.println("Ablesungstabelle erfolgreich erstellt oder existiert bereits.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Tabellen: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void truncateAllTables() throws SQLException {
        String disableForeignKeyChecks = "SET FOREIGN_KEY_CHECKS = 0;";
        String enableForeignKeyChecks = "SET FOREIGN_KEY_CHECKS = 1;";
        String sqlTruncateReadings = "TRUNCATE TABLE readings;";
        String sqlTruncateCustomers = "TRUNCATE TABLE customers;";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(disableForeignKeyChecks); // Disable foreign key checks

            stmt.execute(sqlTruncateReadings);
            System.out.println("Ablesungstabelle erfolgreich geleert.");

            stmt.execute(sqlTruncateCustomers);
            System.out.println("Kundentabelle erfolgreich geleert.");

            stmt.execute(enableForeignKeyChecks);
        } catch (SQLException e) {
            System.err.println("Fehler beim Leeren der Tabellen: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public void removeAllTables() throws SQLException {
        String sqlDropReadings = "DROP TABLE IF EXISTS readings;";
        String sqlDropCustomers = "DROP TABLE IF EXISTS customers;";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlDropReadings);
            System.out.println("Ablesungstabelle erfolgreich entfernt.");

            stmt.execute(sqlDropCustomers);
            System.out.println("Kundentabelle erfolgreich entfernt.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Entfernen der Tabellen: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Datenbankverbindung erfolgreich geschlossen.");
        } else {
            System.out.println("Datenbankverbindung war bereits geschlossen.");
        }
    }
}
