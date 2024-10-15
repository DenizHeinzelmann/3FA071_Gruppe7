package repository;

import interfaces.IDatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection implements IDatabaseConnection {
    private Connection connection;

    // Öffnet DB-Verbindung mit übergebenen Werten von .property file
    @Override
    public Connection openConnection(Properties properties) throws SQLException {
        String url = properties.getProperty(System.getProperty("user.name") + ".db.url");
        String user = properties.getProperty(System.getProperty("user.name") + ".db.user");
        String password = properties.getProperty(System.getProperty("user.name") + ".db.pw");

        String baseUrl = url.substring(0, url.lastIndexOf("/"));
        String dbName = url.substring(url.lastIndexOf("/") + 1);
        connection = DriverManager.getConnection(baseUrl, user, password);
        System.out.println("Connected to database server successfully!");

        // Create the database if it doesn't exist
        this.createDatabase(dbName);

        // Connect to the new database
        connection.setCatalog(dbName);
        System.out.println("Using database: " + dbName);

        // Create tables
        this.createAllTables();
        return connection;
    }

    private void createDatabase(String dbName) throws SQLException {
        String sqlCreateDatabase = "CREATE DATABASE IF NOT EXISTS " + dbName;

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sqlCreateDatabase);
            System.out.println("Database created or already exists: " + dbName);
        }
    }

    //Created die Tables
    @Override
    public void createAllTables() throws SQLException {
        String sqlCreateCustomers = "CREATE TABLE IF NOT EXISTS customers (" +
                "id UUID PRIMARY KEY," +
                "firstname VARCHAR(255)," +
                "lastname VARCHAR(255)," +
                "birthdate DATE," +
                "gender VARCHAR(255)" +
                ");";

        String sqlCreateReadings = "CREATE TABLE IF NOT EXISTS readings (" +
                "id UUID PRIMARY KEY," +
                "customer_id UUID," +
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

    // Closed die Verbindung zur Datenbank
    @Override
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Datenbankverbindung geschlossen.");
        }
    }
/*
    // CRUD operations for Customer
    public void createCustomer(String name, String email) throws SQLException {
        String sql = "INSERT INTO customers (name, email) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
        }
    }

    public Customer readCustomerById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
            }
        }
        return null; // return null if customer is not found
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
    }*/
}

