package repository;

import interfaces.IDatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection implements IDatabaseConnection {
    protected Connection connection;

    @Override
    public Connection openConnection(Properties properties) throws SQLException {
        String url = properties.getProperty(System.getProperty("user.name") + ".db.url");
        String user = properties.getProperty(System.getProperty("user.name") + ".db.user");
        String password = properties.getProperty(System.getProperty("user.name") + ".db.pw");

        String baseUrl = url.substring(0, url.lastIndexOf("/"));
        String dbName = url.substring(url.lastIndexOf("/") + 1);
        connection = DriverManager.getConnection(baseUrl, user, password);
        System.out.println("Connected to database server successfully!");

        // Erstellen Sie die Datenbank, falls sie nicht existiert
        this.createDatabase(dbName);

        // Verbinden Sie sich mit der neuen Datenbank
        connection.setCatalog(dbName);
        System.out.println("Using database: " + dbName);

        // Entfernen Sie die vorhandenen Tabellen, um das Schema zu aktualisieren
        this.removeAllTables();

        // Erstellen Sie die Tabellen mit dem aktuellen Schema
        this.createAllTables();
        return connection;
    }

    private void createDatabase(String dbName) throws SQLException {
        String sqlCreateDatabase = "CREATE DATABASE IF NOT EXISTS " + dbName + " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sqlCreateDatabase);
            System.out.println("Database created or already exists: " + dbName);
        }
    }

    @Override
    public void createAllTables() throws SQLException {
        String sqlCreateCustomers = "CREATE TABLE IF NOT EXISTS customers (" +
                "id VARCHAR(36) PRIMARY KEY," +
                "firstname VARCHAR(255)," +
                "lastname VARCHAR(255)," +
                "birthdate DATE," +
                "gender VARCHAR(10)" +
                ");";

        String sqlCreateReadings = "CREATE TABLE IF NOT EXISTS readings (" +
                "id VARCHAR(36) PRIMARY KEY," +
                "customer_id VARCHAR(36)," +
                "kind_of_meter VARCHAR(50)," +
                "meter_count DECIMAL(10,2)," +
                "date_of_reading DATE," +
                "meter_id VARCHAR(255)," +
                "substitute BOOLEAN," +
                "comment TEXT," +
                "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlCreateCustomers);
            stmt.execute(sqlCreateReadings);
        }
    }

    @Override
    public void truncateAllTables() throws SQLException {
        String sqlTruncateReadings = "TRUNCATE TABLE readings;";
        String sqlTruncateCustomers = "TRUNCATE TABLE customers;";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlTruncateReadings);
            stmt.execute(sqlTruncateCustomers);
        }
    }

    @Override
    public void removeAllTables() throws SQLException {
        String sqlDropReadings = "DROP TABLE IF EXISTS readings;";
        String sqlDropCustomers = "DROP TABLE IF EXISTS customers;";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlDropReadings);
            stmt.execute(sqlDropCustomers);
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Datenbankverbindung geschlossen.");
        }
    }
}
