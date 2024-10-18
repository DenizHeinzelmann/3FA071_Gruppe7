package repository;

import enums.KindOfMeter;
import model.Customer;
import model.Reading;

import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class ReadingRepository implements AutoCloseable {
    private final DatabaseConnection db_connection;
    private final Connection connection;
    private final CustomerRepository customerRepository;

    public ReadingRepository(Properties properties) throws SQLException {
        this.db_connection = new DatabaseConnection();
        this.connection = this.db_connection.openConnection(properties);
        this.customerRepository = new CustomerRepository(properties);
    }

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
            throw new RuntimeException(e);
        }
    }

    public Reading getReading(UUID id) {
        String sql = "SELECT * FROM readings WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setObject(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Reading(
                        UUID.fromString(rs.getString("id")),
                        rs.getBoolean("substitute"),
                        rs.getString("meter_id"),
                        rs.getDouble("meter_count"),
                        KindOfMeter.valueOf(rs.getString("kind_of_meter")),
                        rs.getDate("date_of_reading").toLocalDate(),
                        customerRepository.getCustomer(UUID.fromString(rs.getString("customer_id"))),
                        rs.getString("comment")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public void updateReading(UUID id, Reading reading) {
        String sql = "UPDATE readings SET substitute=?, meter_id=?, meter_count=?, kind_of_meter=?, date_of_reading=?, customer_id=?, comment=? WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setBoolean(1, reading.getSubstitute());
            stmt.setString(2, reading.getMeterId());
            stmt.setDouble(3, reading.getMeterCount());
            stmt.setString(4, reading.getKindOfMeter().name());
            stmt.setDate(5, Date.valueOf(reading.getDateOfReading()));
            stmt.setObject(6, reading.getCustomer().getid());
            stmt.setString(7, reading.getComment());
            stmt.setObject(8, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating reading: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteReading(UUID id) {
        String sql = "DELETE FROM readings WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();  // Vergessenes Ausführen des Löschbefehls
        } catch (SQLException e) {
            System.err.println("Error deleting reading: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        this.db_connection.closeConnection();
    }
}