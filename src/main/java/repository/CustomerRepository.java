package repository;

import model.Customer;

import java.sql.*;
import java.util.Properties;

public class CustomerRepository implements AutoCloseable{
    private final DatabaseConnection db_connection;
    private final Connection connection;

    public CustomerRepository(Properties properties) throws SQLException {
        this.db_connection = new DatabaseConnection();
        this.connection = this.db_connection.openConnection(properties);
    }

    public void createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (firstname, lastname, birthdate, gender) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setDate(3, Date.valueOf(customer.getBirthDate()));
            stmt.setObject(4, customer.getGender());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Customer getCustomerByID(int id) {
        String sql = "SELECT * FROM customers WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(rs.getString("firstname"), rs.getString("lastname"), rs.getDate("birthday").toLocalDate(), rs.getObject("gender"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void updateCustomer(int id, Customer customer) {
        String sql = "UPDATE customers SET firstname=?, lastname=?, birthday=?, gender=? WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setDate(4, Date.valueOf(customer.getBirthDate()));
            stmt.setObject(5, customer.getGender());
            stmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteCustomer(int id) {
        String sql = "DELETE * FROM customers WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() throws Exception {
        this.db_connection.closeConnection();
    }
}
