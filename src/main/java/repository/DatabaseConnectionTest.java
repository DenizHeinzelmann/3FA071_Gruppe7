package repository;

import interfaces.IDatabaseConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Properties;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    public void testOpenConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");

        Connection connection = dbConnection.openConnection(properties);
        assertNotNull("Die Verbindung zur Datenbank sollte nicht null sein.", connection);
    }

    @Test
    public void testCreateCustomer() throws SQLException {
        dbConnection.createCustomer("Max Mustermann", "max@mustermann.com");
        assertTrue("Customer should exist after creation", dbConnection.customerExists(1));
    }

    @Test
    public void testDeleteCustomer() throws SQLException {
        dbConnection.createCustomer("John Doe", "john@doe.com");
        dbConnection.deleteCustomer(1);
        assertTrue("Customer should no longer exist", !dbConnection.customerExists(1));
    }
}
