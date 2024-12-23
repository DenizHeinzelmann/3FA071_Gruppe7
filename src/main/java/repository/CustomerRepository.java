package repository;

import enums.Gender;
import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerRepository implements AutoCloseable {
    private final Connection connection;

    public CustomerRepository() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public UUID createCustomer(Customer customer) {
        UUID id = customer.getid() != null ? customer.getid() : UUID.randomUUID();
        customer.setid(id);

        String sql = "INSERT INTO customers (id, firstname, lastname, birthdate, gender) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());

            if (customer.getBirthDate() != null) {
                stmt.setDate(4, Date.valueOf(customer.getBirthDate()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            if (customer.getGender() != null) {
                stmt.setString(5, customer.getGender().name());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }

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
                        rs.getDate("birthdate") != null ? rs.getDate("birthdate").toLocalDate() : null,
                        rs.getString("gender") != null ? Gender.valueOf(rs.getString("gender")) : null
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving customer", e);
        }
        return null;
    }
    public List<Customer> getAllCustomers() throws SQLException {
        String sql = "SELECT * FROM customers";
        List<Customer> customers = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Customer customer = new Customer(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getDate("birthdate") != null ? rs.getDate("birthdate").toLocalDate() : null,
                        rs.getString("gender") != null ? Gender.valueOf(rs.getString("gender")) : null
                );
                customers.add(customer);
            }
        }
        return customers;
    }

    public void updateCustomer(UUID id, Customer customer) {
        String sql = "UPDATE customers SET firstname=?, lastname=?, birthdate=?, gender=? WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());

            if (customer.getBirthDate() != null) {
                stmt.setDate(3, Date.valueOf(customer.getBirthDate()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            if (customer.getGender() != null) {
                stmt.setString(4, customer.getGender().name());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }

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
    }
}
