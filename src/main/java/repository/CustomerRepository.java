package repository;

import enums.Gender;
import model.Customer;

import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class CustomerRepository implements AutoCloseable {
<<<<<<< HEAD
    protected final DatabaseConnection db_connection;
    protected final Connection connection;
=======
    private final repository.DatabaseConnection db_connection;
    private final Connection connection;
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a

    public CustomerRepository(Properties properties) throws SQLException {
        this.db_connection = new repository.DatabaseConnection();
        this.connection = this.db_connection.openConnection(properties);
    }

    public UUID createCustomer(Customer customer) {
<<<<<<< HEAD
        String sql = "INSERT INTO customers (id, firstname, lastname, birthdate, gender) VALUES (?, ?, ?, ?, ?)";
        UUID id = customer.getid();
        if (id == null) {
            id = UUID.randomUUID();
            customer.setid(id);
        }
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
=======
        String sql = "INSERT INTO customers (id, first_name, last_name, birth_date, gender) VALUES (?, ?, ?, ?, ?)";
        UUID id = UUID.randomUUID(); // Generate a new UUID for the customer
        customer.setid(id); // Set the generated ID in the customer object
        System.out.println("DIE ID IST " + id);


        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setObject(1, id); // Use the generated UUID
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setDate(4, Date.valueOf(customer.getBirthDate()));
            stmt.setString(5, customer.getGender().name());
<<<<<<< HEAD
            stmt.executeUpdate();
            return id;

        } catch (SQLException e) {
            System.err.println("SQL Error in createCustomer: " + e.getMessage());
            throw new RuntimeException(e);
=======

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Inserting customer failed, no rows affected.");
            }

            return id; // Return the ID of the created customer

        } catch (SQLException e) {
            throw new RuntimeException("Error while creating customer", e);
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
        }
    }

    public Customer getCustomer(UUID id) {
        String sql = "SELECT * FROM customers WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
<<<<<<< HEAD
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
=======
                return new Customer(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("first_name"), // Corrected to 'first_name'
                        rs.getString("last_name"),  // Corrected to 'last_name'
                        rs.getDate("birth_date").toLocalDate(), // Corrected to 'birth_date'
                        Gender.valueOf(rs.getString("gender"))
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving customer", e);
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
        }
        return null; // Return null if the customer is not found
    }

    public void updateCustomer(UUID id, Customer customer) {
        String sql = "UPDATE customers SET first_name=?, last_name=?, birth_date=?, gender=? WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setDate(3, Date.valueOf(customer.getBirthDate()));
<<<<<<< HEAD
            stmt.setString(4, customer.getGender().name());
            stmt.setString(5, id.toString());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("SQL Error in updateCustomer: " + e.getMessage());
            throw new RuntimeException(e);
=======
            stmt.setString(4, customer.getGender().name()); // Use String for gender
            stmt.setObject(5, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating customer", e);
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
        }
    }

    public void deleteCustomer(UUID id) {
        String sql = "DELETE FROM customers WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();

        } catch (SQLException e) {
<<<<<<< HEAD
            System.err.println("SQL Error in deleteCustomer: " + e.getMessage());
            throw new RuntimeException(e);
=======
            throw new RuntimeException("Error deleting customer", e);
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
        }
    }

    @Override
    public void close() throws Exception {
        this.db_connection.closeConnection();
    }
}