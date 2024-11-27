package repository;

import enums.Gender;
import model.Customer;

import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class CustomerRepository implements AutoCloseable {
    protected final DatabaseConnection db_connection;
    protected final Connection connection;

    public CustomerRepository(Properties properties) throws SQLException {
        this.db_connection = new DatabaseConnection();
        this.connection = this.db_connection.openConnection(properties);
    }

    public UUID createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (id, firstname, lastname, birthdate, gender) VALUES (?, ?, ?, ?, ?)";
        UUID id = customer.getid();
        if (id == null) {
            id = UUID.randomUUID();
            customer.setid(id);
        }
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setDate(4, Date.valueOf(customer.getBirthDate()));
            stmt.setString(5, customer.getGender().name());
            stmt.executeUpdate();
            return id;

        } catch (SQLException e) {
            System.err.println("SQL Error in createCustomer: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Customer getCustomer(UUID id) {
        String sql = "SELECT * FROM customers WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String customerIdStr = rs.getString("id");
                UUID customerId = UUID.fromString(customerIdStr);

                Customer customer = new Customer(
                        customerId,
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getDate("birthdate").toLocalDate(),
                        Gender.valueOf(rs.getString("gender"))
                );
                return customer;
            }

        } catch (SQLException e) {
            System.err.println("SQL Error in getCustomer: " + e.getMessage());
            throw new RuntimeException(e);
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
            System.err.println("SQL Error in updateCustomer: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteCustomer(UUID id) {
        String sql = "DELETE FROM customers WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("SQL Error in deleteCustomer: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        this.db_connection.closeConnection();
    }
}