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
        // Lade die Properties aus der Datei
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new FileNotFoundException("Die Datei 'database.properties' wurde nicht gefunden!");
            }
            properties.load(input);
        }

        databaseConnection = DatabaseConnection.getInstance();
        connection = databaseConnection.openConnection(properties);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            databaseConnection.removeAllTables(); // Ensure this runs before the connection is closed
            databaseConnection.closeConnection(); // Close the connection after cleaning up
        } else {
            System.out.println("Die Verbindung war bereits geschlossen.");
        }
    }


    @Test
    void testOpenConnection() throws SQLException {
        assertNotNull(connection, "Die Verbindung sollte erfolgreich hergestellt worden sein.");
        assertFalse(connection.isClosed(), "Die Verbindung sollte geöffnet sein.");
    }

    @Test
    void testCreateAllTables() throws SQLException {
        databaseConnection.createAllTables();

        // Prüfen, ob die Tabellen existieren
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'CUSTOMERS'");
            assertTrue(rs.next(), "Die Tabelle 'customers' sollte existieren.");

            rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'READINGS'");
            assertTrue(rs.next(), "Die Tabelle 'readings' sollte existieren.");
        }
    }

    @Test
    void testInsertDataIntoCustomersTable() throws SQLException {
        databaseConnection.createAllTables();

        String sqlInsert = "INSERT INTO customers (id, firstname, lastname, birthdate, gender) VALUES ('123e4567-e89b-12d3-a456-426614174000', 'Max', 'Mustermann', '1990-01-01', 'M')";
        try (Statement stmt = connection.createStatement()) {
            int rowsInserted = stmt.executeUpdate(sqlInsert);
            assertEquals(1, rowsInserted, "Eine Zeile sollte erfolgreich in die Tabelle 'customers' eingefügt werden.");
        }

        // Überprüfen, ob die Daten korrekt eingefügt wurden
        String sqlSelect = "SELECT * FROM customers WHERE id = '123e4567-e89b-12d3-a456-426614174000'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sqlSelect)) {
            assertTrue(rs.next(), "Es sollte ein Eintrag mit der angegebenen ID existieren.");
            assertEquals("Max", rs.getString("firstname"), "Der Vorname sollte 'Max' sein.");
            assertEquals("Mustermann", rs.getString("lastname"), "Der Nachname sollte 'Mustermann' sein.");
        }
    }

    @Test
    void testTruncateAllTables() throws SQLException {
        databaseConnection.createAllTables();

        // Daten in die Tabellen einfügen
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO customers (id, firstname, lastname, birthdate, gender) VALUES ('123e4567-e89b-12d3-a456-426614174000', 'Max', 'Mustermann', '1990-01-01', 'M')");
            stmt.executeUpdate("INSERT INTO readings (id, customer_id, kind_of_meter, meter_count, date_of_reading, meter_id, substitute, comment) VALUES ('223e4567-e89b-12d3-a456-426614174001', '123e4567-e89b-12d3-a456-426614174000', 'STROM', 100.50, '2024-01-01', 'M123', false, 'Initial Reading')");
        }

        databaseConnection.truncateAllTables();

        // Überprüfen, ob die Tabellen geleert wurden
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM customers");
            assertTrue(rs.next());
            assertEquals(0, rs.getInt("count"), "Die Tabelle 'customers' sollte geleert sein.");

            rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM readings");
            assertTrue(rs.next());
            assertEquals(0, rs.getInt("count"), "Die Tabelle 'readings' sollte geleert sein.");
        }
    }

    @Test
    void testRemoveAllTables() throws SQLException {
        databaseConnection.createAllTables();
        databaseConnection.removeAllTables();

        // Überprüfen, ob die Tabellen entfernt wurden
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'CUSTOMERS'");
            assertFalse(rs.next(), "Die Tabelle 'customers' sollte nicht mehr existieren.");

            rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'READINGS'");
            assertFalse(rs.next(), "Die Tabelle 'readings' sollte nicht mehr existieren.");
        }
    }

    @Test
    void testCloseConnection() throws SQLException {
        databaseConnection.closeConnection();
        assertTrue(connection.isClosed(), "Die Verbindung sollte geschlossen sein.");
    }
}
