package repository;

import interfaces.IDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
                "customer_id INT," +
                "reading_type VARCHAR(50)," +
                "reading_value DECIMAL(10,2)," +
                "reading_date DATE," +
                "unit VARCHAR(10)," +
                "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL" +
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
            stmt.execute(sqlTruncateReadings);
            stmt.execute(sqlTruncateCustomers);
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

    // CRUD operations for Customer
    public void createCustomer(String name, String email) throws SQLException {
        String sql = "INSERT INTO customers (name, email) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
        }
    }

    public void updateCustomer(int id, String name, String email) throws SQLException {
        String sql = "UPDATE customers SET name = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    public void deleteCustomer(int id) throws SQLException {
        String sqlNullifyReadings = "UPDATE readings SET customer_id = NULL WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlNullifyReadings)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

        String sqlDeleteCustomer = "DELETE FROM customers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlDeleteCustomer)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Customer> readAllCustomers() throws SQLException {
        String sql = "SELECT * FROM customers";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            List<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                Customer customer = new Customer(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                customers.add(customer);
            }
            return customers;
        }
    }

    // CRUD operations for Readings
    public void createReading(int customerId, String readingType, double readingValue, Date readingDate, String unit) throws SQLException {
        if (!customerExists(customerId)) {
            // Handle customer creation or throw exception
            throw new SQLException("Customer does not exist. Cannot create reading.");
        }

        String sql = "INSERT INTO readings (customer_id, reading_type, reading_value, reading_date, unit) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, readingType);
            stmt.setDouble(3, readingValue);
            stmt.setDate(4, new java.sql.Date(readingDate.getTime()));
            stmt.setString(5, unit);
            stmt.executeUpdate();
        }
    }

    public boolean customerExists(int customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
