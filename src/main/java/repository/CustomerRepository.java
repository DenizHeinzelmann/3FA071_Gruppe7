package repository;

import enums.Gender;
import model.Customer;

import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class CustomerRepository implements AutoCloseable {
    private final DatabaseConnection db_connection;
    private final Connection connection;

    public CustomerRepository(Properties properties) throws SQLException {
        this.db_connection = new DatabaseConnection();
        this.connection = this.db_connection.openConnection(properties);
    }

    public UUID createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (id, firstname, lastname, birthdate, gender) VALUES (?, ?, ?, ?, ?)";
        UUID id = UUID.randomUUID();
        customer.setid(id);

        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setDate(4, Date.valueOf(customer.getBirthDate()));
            stmt.setString(5, customer.getGender().name());
            stmt.executeUpdate();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating customer", e);
        }
    }

    public Customer getCustomer(UUID id) {
        String sql = "SELECT * FROM customers WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getDate("birthdate").toLocalDate(),
                        Gender.valueOf(rs.getString("gender"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving customer", e);
        }
        return null;
    }

    public void updateCustomer(UUID id, Customer customer) {
        String sql = "UPDATE customers SET firstname=?, lastname=?, birthdate=?, gender=? WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setDate(3, Date.valueOf(customer.getBirthDate()));
            stmt.setString(4, customer.getGender().name());
            stmt.setString(5, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    public void deleteCustomer(UUID id) {
        String sql = "DELETE FROM customers WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            db_connection.closeConnection();
        }
    }
}
