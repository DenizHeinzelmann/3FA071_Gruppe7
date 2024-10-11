package repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.*;

public class DatabaseConnectionTest {

    private DatabaseConnection dbConnection;

    @Before
    public void setUp() throws SQLException {
        dbConnection = new DatabaseConnection();

        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");

        dbConnection.openConnection(properties); // Verbindung öffnen
    }

    @After
    public void tearDown() {
        try {
            dbConnection.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateCustomer() throws SQLException {
        dbConnection.createCustomer("Max Mustermann", "max@mustermann.com");
        assertTrue("Customer should exist after creation", dbConnection.customerExists(1));
    }

    @org.testng.annotations.Test
    public void testReadCustomerById() throws SQLException {
        dbConnection.createCustomer("John Doe", "john@doe.com");
        Customer customer = dbConnection.readCustomerById(1);
        assertNotNull("Customer should be found by ID", customer);
        assertEquals("John Doe", customer.getName());
        assertEquals("john@doe.com", customer.getEmail());
    }

    @Test
    public void testUpdateCustomer() throws SQLException {
        dbConnection.createCustomer("Jane Doe", "jane@doe.com");
        dbConnection.updateCustomer(1, "Jane Updated", "jane@updated.com");
        Customer updatedCustomer = dbConnection.readCustomerById(1);
        assertEquals("Jane Updated", updatedCustomer.getName());
        assertEquals("jane@updated.com", updatedCustomer.getEmail());
    }

    @Test
    public void testDeleteCustomer() throws SQLException {
        dbConnection.createCustomer("John Doe", "john@doe.com");
        dbConnection.deleteCustomer(1);
        assertFalse("Customer should no longer exist", dbConnection.customerExists(1));
    }
}