package repository;

import enums.KindOfMeter;
import model.Customer;
import model.Reading;

import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class ReadingRepository implements AutoCloseable {
<<<<<<< HEAD
    protected final DatabaseConnection db_connection;
    protected final Connection connection;
    protected final CustomerRepository customerRepository;
=======
    private final DatabaseConnection db_connection;
    private final Connection connection;
    private final CustomerRepository customerRepository;
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a

    public ReadingRepository(Properties properties) throws SQLException {
        this.db_connection = new DatabaseConnection();
        this.connection = this.db_connection.openConnection(properties);
        this.customerRepository = new CustomerRepository(properties);
    }

<<<<<<< HEAD
    public UUID createReading(Reading reading) {
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
=======
    public void createReading(Reading reading) {
        String sql = "INSERT INTO readings (id, substitute, meter_id, meter_count, kind_of_meter, date_of_reading, customer_id, comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setObject(1, reading.getid().toString());
            stmt.setBoolean(2, reading.getSubstitute());
            stmt.setString(3, reading.getMeterId());
            stmt.setDouble(4, reading.getMeterCount());
            stmt.setString(5, reading.getKindOfMeter().toString());
            stmt.setDate(6, java.sql.Date.valueOf(reading.getDateOfReading()));
            stmt.setObject(7, reading.getCustomer().getid().toString());
            stmt.setString(8, reading.getComment());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating reading: " + e.getMessage());
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
            throw new RuntimeException(e);
        }
    }

    public Reading getReading(UUID id) {
        String sql = "SELECT * FROM readings WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
<<<<<<< HEAD
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
=======
            stmt.setObject(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Reading(
                        UUID.fromString(rs.getString("id")),
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
                        rs.getBoolean("substitute"),
                        rs.getString("meter_id"),
                        rs.getDouble("meter_count"),
                        KindOfMeter.valueOf(rs.getString("kind_of_meter")),
                        rs.getDate("date_of_reading").toLocalDate(),
<<<<<<< HEAD
                        customer,
                        rs.getString("comment")
                );
                reading.setid(readingId);
                return reading;
=======
                        customerRepository.getCustomer(UUID.fromString(rs.getString("customer_id"))),
                        rs.getString("comment")
                );
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getReading: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }


    public void updateReading(UUID id, Reading reading) {
<<<<<<< HEAD
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
=======
        String sql = "UPDATE readings SET substitute=?, meter_id=?, meter_count=?, kind_of_meter=?, date_of_reading=?, customer_id=?, comment=? WHERE id=?";
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, customerId.toString());
            stmt.setString(2, reading.getKindOfMeter().name());
            stmt.setDouble(3, reading.getMeterCount());
<<<<<<< HEAD
            stmt.setDate(4, Date.valueOf(reading.getDateOfReading()));
            stmt.setString(5, reading.getMeterId());
            stmt.setBoolean(6, reading.getSubstitute());
            stmt.setString(7, reading.getComment());
            stmt.setString(8, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error in updateReading: " + e.getMessage());
=======
            stmt.setString(4, reading.getKindOfMeter().name());
            stmt.setDate(5, Date.valueOf(reading.getDateOfReading()));
            stmt.setObject(6, reading.getCustomer().getid());
            stmt.setString(7, reading.getComment());
            stmt.setObject(8, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating reading: " + e.getMessage());
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
            throw new RuntimeException(e);
        }
    }

    public void deleteReading(UUID id) {
        String sql = "DELETE FROM readings WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
<<<<<<< HEAD
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error in deleteReading: " + e.getMessage());
=======
            stmt.setObject(1, id);
            stmt.executeUpdate();  // Vergessenes Ausführen des Löschbefehls
        } catch (SQLException e) {
            System.err.println("Error deleting reading: " + e.getMessage());
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        this.db_connection.closeConnection();
    }

    public void deleteReadingById(UUID id) {
        String sql = "DELETE FROM readings WHERE id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setObject(1, id); // Setze die ID als Parameter
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Reading mit ID " + id + " wurde erfolgreich gelöscht.");
            } else {
                System.out.println("Reading mit ID " + id + " wurde nicht gefunden.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Readings: " + e.getMessage(), e);
        }
    }
}