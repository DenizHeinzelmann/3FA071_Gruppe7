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
    private final CustomerRepository  customerRepository;

    public ReadingRepository(Properties properties) throws SQLException {
        this.db_connection = new DatabaseConnection();
        this.connection = this.db_connection.openConnection(properties);
        this.customerRepository = new CustomerRepository(properties);
    }

    public void createReading(Customer customer) {
        String sql = "INSERT INTO readings (substitute,meterId,meterCount,kindOfMeter,dateOfReading,customer_id,comment) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setDate(3, Date.valueOf(customer.getBirthDate()));
            stmt.setObject(4, customer.getGender().name());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Reading getReading(int id) {
        String sql = "SELECT comment, customer_id, dateOfReading, kindOfMeter, meterCount, meterId, substitute FROM reading WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Customer customer = this.customerRepository.getCustomer(rs.getObject("customer_id",UUID.class));
                return new Reading(rs.getBoolean("substitute"),rs.getString("meterId"),rs.getDouble("meterCount"), KindOfMeter.valueOf(rs.getString("kindOfMeter")), rs.getDate("dateOfReading").toLocalDate(),customer ,rs.getString("comment"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void updateReading(UUID id, Reading reading) {
        String sql = "UPDATE readings set substitute=?, meterId=?, meterCount=?, kindOfMeter=?, dateOfReading=?, customer_id=?, comment=? WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setBoolean(1, reading.getSubstitute());
            stmt.setString(2, reading.getMeterId());
            stmt.setDouble(3, reading.getMeterCount());
            stmt.setObject(4, reading.getKindOfMeter().name());
            stmt.setDate(5, Date.valueOf(reading.getDateOfReading()));
            stmt.setObject(6, reading.getid());

            stmt.setObject(7, id);
           

            stmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteReading(UUID id) {
        String sql = "DELETE * FROM Reading WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setObject(1, id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        this.db_connection.closeConnection();
    }
}