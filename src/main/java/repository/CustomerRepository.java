package repository;

import model.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class CustomerRepository {
    private final DatabaseConnection db_connection;
    private final Connection connection;

    public CustomerRepository(Properties properties) throws SQLException {
        this.db_connection = new DatabaseConnection();
        this.connection = this.db_connection.openConnection(properties);
    }

    void createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (firstname, lastname, birthdate, gender) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setDate(3, Date.valueOf(customer.getBirthDate()));
            stmt.setString(4, customer.getGender().toString());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void getCustomer(Customer customer) {
        String sql = "INSERT INTO customers (name) VALUES (?, ?)";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void updateCustomer(Customer customer) {

    }

    void deleteCustomer(Customer customer) {

    }


}
