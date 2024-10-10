package repository;

import interfaces.IDatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection implements IDatabaseConnection {
    private Connection connection;

    @Override
    public Connection openConnection(Properties properties) throws SQLException {
        String url = properties.getProperty(System.getProperty("user.name") + ".db.url");
        String user = properties.getProperty(System.getProperty("user.name") + ".db.user");
        String password = properties.getProperty(System.getProperty("user.name") + ".db.pw");

        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Verbindung zur Datenbank erfolgreich!");
        return connection;
    }

    @Override
    public void createAllTables() throws SQLException {
        String sqlCreateCustomers = "CREATE TABLE IF NOT EXISTS customers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255)," +
                "email VARCHAR(255)" +
                ");";

        String sqlCreateReadings = "CREATE TABLE IF NOT EXISTS readings (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "customer_id INT NOT NULL," +
                "reading_type VARCHAR(50)," +
                "reading_value DECIMAL(10,2)," +
                "reading_date DATE," +
                "unit VARCHAR(10)," +
                "FOREIGN KEY (customer_id) REFERENCES customers(id)" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlCreateCustomers);
            stmt.execute(sqlCreateReadings);
        }
    }

    @Override
    public void truncateAllTables() throws SQLException {
        String sqlTruncateCustomers = "TRUNCATE TABLE customers;";
        String sqlTruncateReadings = "TRUNCATE TABLE readings;";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlTruncateCustomers);
            stmt.execute(sqlTruncateReadings);
        }
    }

    @Override
    public void removeAllTables() throws SQLException {
        String sqlDropCustomers = "DROP TABLE IF EXISTS customers;";
        String sqlDropReadings = "DROP TABLE IF EXISTS readings;";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlDropCustomers);
            stmt.execute(sqlDropReadings);
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