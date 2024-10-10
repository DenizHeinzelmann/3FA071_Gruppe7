package repository;

import interfaces.IDatabaseConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.SQLException;
import static org.junit.Assert.assertNotNull;

public class DatabaseConnectionTest {

    private IDatabaseConnection dbConnection;

    @Before
    public void setUp() {
        dbConnection = new DatabaseConnection();
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
    public void testOpenConnection() {
        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");

        Connection connection = null;

        try {
            connection = dbConnection.openConnection(properties);
            assertNotNull("Die Verbindung zur Datenbank sollte nicht null sein.", connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
