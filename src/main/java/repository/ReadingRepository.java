package repository;

import enums.KindOfMeter;
import model.Customer;
import model.Reading;

import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class ReadingRepository implements AutoCloseable {
    protected final DatabaseConnection db_connection;
    protected final Connection connection;
    protected final CustomerRepository customerRepository;

    public ReadingRepository(Properties properties) throws SQLException {
        this.db_connection = new DatabaseConnection();
        this.connection = this.db_connection.openConnection(properties);
        this.customerRepository = new CustomerRepository(properties);
    }

    public UUID createReading(Reading reading) {
        // Überprüfen, ob ein Kunde vorhanden ist
        if (reading.getCustomer() == null) {
            throw new IllegalArgumentException("Reading must have a customer.");
        }

        UUID customerId = reading.getCustomer().getid();
        if (customerId == null) {
            customerId = UUID.randomUUID();
            reading.getCustomer().setid(customerId);
        }

        // Überprüfen, ob der Kunde in der DB existiert
        Customer customerInDb = customerRepository.getCustomer(customerId);
        if (customerInDb == null) {
            // Kunde existiert nicht, also hinzufügen
            customerRepository.createCustomer(reading.getCustomer());
        }

        String sql = "INSERT INTO readings (id, customer_id, kind_of_meter, meter_count, date_of_reading, meter_id, substitute, comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        UUID id = UUID.randomUUID();
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString()); // UUID als String setzen
            stmt.setString(2, customerId.toString()); // UUID als String setzen
            stmt.setString(3, reading.getKindOfMeter().name());
            stmt.setDouble(4, reading.getMeterCount());
            stmt.setDate(5, Date.valueOf(reading.getDateOfReading()));
            stmt.setString(6, reading.getMeterId());
            stmt.setBoolean(7, reading.getSubstitute());
            stmt.setString(8, reading.getComment());
            stmt.executeUpdate();
            return id;
        } catch (SQLException e) {
            System.err.println("SQL Error in createReading: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Reading getReading(UUID id) {
        String sql = "SELECT * FROM readings WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String readingIdStr = rs.getString("id");
                UUID readingId = UUID.fromString(readingIdStr);

                String customerIdStr = rs.getString("customer_id");
                Customer customer = null;
                if (customerIdStr != null) {
                    UUID customerId = UUID.fromString(customerIdStr);
                    customer = customerRepository.getCustomer(customerId);
                }

                Reading reading = new Reading(
                        rs.getBoolean("substitute"),
                        rs.getString("meter_id"),
                        rs.getDouble("meter_count"),
                        KindOfMeter.valueOf(rs.getString("kind_of_meter")),
                        rs.getDate("date_of_reading").toLocalDate(),
                        customer,
                        rs.getString("comment")
                );
                reading.setid(readingId);
                return reading;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getReading: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    public void updateReading(UUID id, Reading reading) {
        if (reading.getCustomer() == null) {
            throw new IllegalArgumentException("Reading must have a customer.");
        }

        UUID customerId = reading.getCustomer().getid();
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null.");
        }

        Customer customerInDb = customerRepository.getCustomer(customerId);
        if (customerInDb == null) {
            customerRepository.createCustomer(reading.getCustomer());
        }

        String sql = "UPDATE readings SET customer_id=?, kind_of_meter=?, meter_count=?, date_of_reading=?, meter_id=?, substitute=?, comment=? WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, customerId.toString());
            stmt.setString(2, reading.getKindOfMeter().name());
            stmt.setDouble(3, reading.getMeterCount());
            stmt.setDate(4, Date.valueOf(reading.getDateOfReading()));
            stmt.setString(5, reading.getMeterId());
            stmt.setBoolean(6, reading.getSubstitute());
            stmt.setString(7, reading.getComment());
            stmt.setString(8, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error in updateReading: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteReading(UUID id) {
        String sql = "DELETE FROM readings WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error in deleteReading: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        this.db_connection.closeConnection();
    }
}