package repository;

import enums.KindOfMeter;
import model.Customer;
import model.Reading;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReadingRepository implements AutoCloseable {
    private final Connection connection;
    private final CustomerRepository customerRepository;

    public ReadingRepository() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.customerRepository = new CustomerRepository();
    }

    public UUID createReading(Reading reading) {
        if (reading.getCustomer() == null) {
            throw new IllegalArgumentException("Reading must have a customer.");
        }

        UUID customerId = reading.getCustomer().getid();
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null for a reading.");
        }

        Customer customerInDb = customerRepository.getCustomer(customerId);
        if (customerInDb == null) {
            throw new IllegalArgumentException("Customer with ID " + customerId + " does not exist.");
        }

        String sql = "INSERT INTO readings (id, customer_id, kind_of_meter, meter_count, date_of_reading, meter_id, substitute, comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        UUID id = UUID.randomUUID();
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.setString(2, customerId.toString());
            stmt.setString(3, reading.getKindOfMeter().name());
            stmt.setDouble(4, reading.getMeterCount());
            stmt.setDate(5, Date.valueOf(reading.getDateOfReading()));
            stmt.setString(6, reading.getMeterId());
            stmt.setBoolean(7, reading.getSubstitute());
            stmt.setString(8, reading.getComment());
            stmt.executeUpdate();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating reading", e);
        }
    }

    public Reading getReading(UUID id) {
        String sql = "SELECT * FROM readings WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID customerId = UUID.fromString(rs.getString("customer_id"));
                Customer customer = customerRepository.getCustomer(customerId);

                Reading reading = new Reading(
                        rs.getBoolean("substitute"),
                        rs.getString("meter_id"),
                        rs.getDouble("meter_count"),
                        KindOfMeter.valueOf(rs.getString("kind_of_meter")),
                        rs.getDate("date_of_reading").toLocalDate(),
                        customer,
                        rs.getString("comment")
                );
                reading.setid(id);
                return reading;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving reading", e);
        }
        return null;
    }

    public List<Reading> getReadingsByCustomer(UUID customerId) throws SQLException {
        String sql = "SELECT * FROM readings WHERE customer_id = ?";
        List<Reading> readings = new ArrayList<>();
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, customerId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reading reading = new Reading(
                            rs.getBoolean("substitute"),
                            rs.getString("meter_id"),
                            rs.getDouble("meter_count"),
                            KindOfMeter.valueOf(rs.getString("kind_of_meter")),
                            rs.getDate("date_of_reading").toLocalDate(),
                            customerRepository.getCustomer(customerId), // Hole den Kunden
                            rs.getString("comment")
                    );
                    reading.setid(UUID.fromString(rs.getString("id")));
                    readings.add(reading);
                }
            }
        }
        return readings;
    }

    public List<Reading> getAllReadings() throws SQLException {
        String sql = "SELECT * FROM readings";
        List<Reading> readings = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                UUID customerId = UUID.fromString(rs.getString("customer_id"));
                Customer customer = customerRepository.getCustomer(customerId);
                Reading reading = new Reading(
                        rs.getBoolean("substitute"),
                        rs.getString("meter_id"),
                        rs.getDouble("meter_count"),
                        KindOfMeter.valueOf(rs.getString("kind_of_meter")),
                        rs.getDate("date_of_reading").toLocalDate(),
                        customer,
                        rs.getString("comment")
                );
                reading.setid(UUID.fromString(rs.getString("id")));
                readings.add(reading);
            }
        }
        return readings;
    }

    public List<Reading> getAllReadingsInRange(String start, String end, String kindOfMeter) {
        List<Reading> readings = new ArrayList<>();
        String sql = "Select * from readings Where (date_of_reading >= ? AND date_of_reading <= ?);";

        if (kindOfMeter != null) {
            sql = "SELECT * FROM readings WHERE (date_of_reading >= ? AND date_of_reading <= ?) AND kind_of_meter = ?;";
        }

        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            if (kindOfMeter != null) {
                stmt.setString(3, kindOfMeter);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID customerId = UUID.fromString(rs.getString("customer_id"));
                    Customer customer = customerRepository.getCustomer(customerId);
                    Reading reading = new Reading(
                            rs.getBoolean("substitute"),
                            rs.getString("meter_id"),
                            rs.getDouble("meter_count"),
                            KindOfMeter.valueOf(rs.getString("kind_of_meter")),
                            rs.getDate("date_of_reading").toLocalDate(),
                            customer,
                            rs.getString("comment")
                    );
                    reading.setid(UUID.fromString(rs.getString("id")));
                    readings.add(reading);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return readings;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateReading(UUID id, Reading reading) {
        if (reading.getCustomer() == null) {
            throw new IllegalArgumentException("Reading must have a customer.");
        }

        UUID customerId = reading.getCustomer().getid();
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null.");
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
            throw new RuntimeException("Error updating reading", e);
        }
    }

    public void deleteReading(UUID id) {
        String sql = "DELETE FROM readings WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reading", e);
        }
    }

    @Override
    public void close() throws SQLException {
    }
}
