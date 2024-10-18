package repository;

import enums.Gender;
import model.Customer;

import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class CustomerRepository implements AutoCloseable {
    private final repository.DatabaseConnection db_connection;
    private final Connection connection;

    public CustomerRepository(Properties properties) throws SQLException {
        this.db_connection = new repository.DatabaseConnection();
        this.connection = this.db_connection.openConnection(properties);
    }

    public UUID createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (id, first_name, last_name, birth_date, gender) VALUES (?, ?, ?, ?, ?)";
        UUID id = UUID.randomUUID(); // Generate a new UUID for the customer
        customer.setid(id); // Set the generated ID in the customer object

        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setObject(1, id); // Use the generated UUID
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setDate(4, Date.valueOf(customer.getBirthDate()));
            stmt.setString(5, customer.getGender().name());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Inserting customer failed, no rows affected.");
            }

            return id; // Return the ID of the created customer

        } catch (SQLException e) {
            throw new RuntimeException("Error while creating customer", e);
        }
    }

    public Customer getCustomer(UUID id) {
        String sql = "SELECT * FROM customers WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getString("first_name"), // Corrected to 'first_name'
                        rs.getString("last_name"),  // Corrected to 'last_name'
                        rs.getDate("birth_date").toLocalDate(), // Corrected to 'birth_date'
                        Gender.valueOf(rs.getString("gender"))
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving customer", e);
        }
        return null; // Return null if the customer is not found
    }

    public void updateCustomer(UUID id, Customer customer) {
        String sql = "UPDATE customers SET first_name=?, last_name=?, birth_date=?, gender=? WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setDate(3, Date.valueOf(customer.getBirthDate()));
            stmt.setString(4, customer.getGender().name()); // Use String for gender
            stmt.setObject(5, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    public void deleteCustomer(UUID id) {
        String sql = "DELETE FROM customers WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }

    @Override
    public void close() throws Exception {
        this.db_connection.closeConnection();
    }
}
