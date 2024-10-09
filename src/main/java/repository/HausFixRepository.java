package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class HausFixRepository {
        private static final String URL = "jdbc:mariadb://localhost:3306/hausfix_db";
        private static final String USER = "root"; // Benutzername
        private static final String PASSWORD = "hausverwaltung"; // Passwort

        public static Connection getConnection() {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Verbindung zur Datenbank erfolgreich!");
            } catch (SQLException e) {
                System.out.println("Fehler bei der Verbindung zur Datenbank: " + e.getMessage());
            }
            return connection;
        }
    }