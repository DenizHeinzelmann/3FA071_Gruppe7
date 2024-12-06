package repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    private DatabaseConnection databaseConnection;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new FileNotFoundException("'database.properties' not found!");
            }
            properties.load(input);
        }

        databaseConnection = DatabaseConnection.getInstance();
        connection = databaseConnection.openConnection(properties);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            databaseConnection.removeAllTables();
            databaseConnection.closeConnection();
        } else {
            System.out.println("Connection was already closed.");
        }
    }

    @Test
    void testOpenConnection() throws SQLException {
        assertNotNull(connection, "Connection should be successfully established.");
        assertFalse(connection.isClosed(), "Connection should be open.");
    }

    @Test
    void testCreateAllTables() throws SQLException {
        databaseConnection.createAllTables();

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'CUSTOMERS'");
            assertTrue(rs.next(), "Table 'customers' should exist.");

            rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'READINGS'");
            assertTrue(rs.next(), "Table 'readings' should exist.");
        }
    }

    @Test
    void testInsertDataIntoCustomersTable() throws SQLException {
        databaseConnection.createAllTables();

        String sqlInsert = "INSERT INTO customers (id, firstname, lastname, birthdate, gender) VALUES ('123e4567-e89b-12d3-a456-426614174000', 'Max', 'Mustermann', '1990-01-01', 'M')";
        try (Statement stmt = connection.createStatement()) {
            int rowsInserted = stmt.executeUpdate(sqlInsert);
            assertEquals(1, rowsInserted, "One row should be successfully inserted into 'customers'.");
        }

        String sqlSelect = "SELECT * FROM customers WHERE id = '123e4567-e89b-12d3-a456-426614174000'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sqlSelect)) {
            assertTrue(rs.next(), "An entry with the given ID should exist.");
            assertEquals("Max", rs.getString("firstname"), "First name should be 'Max'.");
            assertEquals("Mustermann", rs.getString("lastname"), "Last name should be 'Mustermann'.");
        }
    }

    @Test
    void testTruncateAllTables() throws SQLException {
        databaseConnection.createAllTables();

        // Insert data into tables
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO customers (id, firstname, lastname, birthdate, gender) VALUES ('123e4567-e89b-12d3-a456-426614174000', 'Max', 'Mustermann', '1990-01-01', 'M')");
            stmt.executeUpdate("INSERT INTO readings (id, customer_id, kind_of_meter, meter_count, date_of_reading, meter_id, substitute, comment) VALUES ('223e4567-e89b-12d3-a456-426614174001', '123e4567-e89b-12d3-a456-426614174000', 'STROM', 100.50, '2024-01-01', 'M123', false, 'Initial Reading')");
        }

        databaseConnection.truncateAllTables();

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM customers");
            assertTrue(rs.next());
            assertEquals(0, rs.getInt("count"), "'customers' table should be empty.");

            rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM readings");
            assertTrue(rs.next());
            assertEquals(0, rs.getInt("count"), "'readings' table should be empty.");
        }
    }

    @Test
    void testRemoveAllTables() throws SQLException {
        databaseConnection.createAllTables();
        databaseConnection.removeAllTables();

        // Check if tables are removed
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'CUSTOMERS'");
            assertFalse(rs.next(), "'customers' table should no longer exist.");

            rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'READINGS'");
            assertFalse(rs.next(), "'readings' table should no longer exist.");
        }
    }

    @Test
    void testCloseConnection() throws SQLException {
        databaseConnection.closeConnection();
        assertTrue(connection.isClosed(), "Connection should be closed.");
    }
}