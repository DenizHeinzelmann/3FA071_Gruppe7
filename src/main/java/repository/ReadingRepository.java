package repository;

import enums.KindOfMeter;
import model.AnalysisData;
import model.Customer;
import model.Reading;

import java.sql.*;
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

        public List<AnalysisData> getAnalysisData(int periodYears) {
            List<AnalysisData> analysisList = new ArrayList<>();
            String sql;
            // Bei 1 Jahr gruppiere nach Monat (YYYY-MM), ansonsten nach Jahr (YYYY)
            if (periodYears == 1) {
                sql = "SELECT kind_of_meter, DATE_FORMAT(date_of_reading, '%Y-%m') AS periodLabel, AVG(meter_count) AS avgValue " +
                        "FROM readings " +
                        "WHERE date_of_reading >= CURDATE() - INTERVAL 1 YEAR " +
                        "GROUP BY kind_of_meter, periodLabel " +
                        "ORDER BY periodLabel";
            } else {
                sql = "SELECT kind_of_meter, YEAR(date_of_reading) AS periodLabel, AVG(meter_count) AS avgValue " +
                        "FROM readings " +
                        "WHERE date_of_reading >= CURDATE() - INTERVAL ? YEAR " +
                        "GROUP BY kind_of_meter, periodLabel " +
                        "ORDER BY periodLabel";
            }

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (periodYears != 1) {
                    stmt.setInt(1, periodYears);
                }
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String meterType = rs.getString("kind_of_meter");
                    String periodLabel = rs.getString("periodLabel");
                    double avgValue = rs.getDouble("avgValue");
                    analysisList.add(new AnalysisData(meterType, periodLabel, avgValue));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving analysis data", e);
            }
            return analysisList;
        }


    // Ermöglicht das Anlegen eines Readings auch ohne zugeordneten Customer.
    public UUID createReading(Reading reading) {
        String sql = "INSERT INTO readings (id, customer_id, kind_of_meter, meter_count, date_of_reading, meter_id, substitute, comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        UUID id = UUID.randomUUID();
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            // Falls ein Customer vorhanden ist, setze dessen ID, ansonsten NULL
            if (reading.getCustomer() != null && reading.getCustomer().getid() != null) {
                stmt.setString(2, reading.getCustomer().getid().toString());
            } else {
                stmt.setNull(2, Types.CHAR);
            }
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
                String customerIdStr = rs.getString("customer_id");
                Customer customer = null;
                if (customerIdStr != null && !customerIdStr.isEmpty()) {
                    customer = customerRepository.getCustomer(UUID.fromString(customerIdStr));
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
                reading.setid(id);
                return reading;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving reading (RepositoryClass)", e);
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
                            customerRepository.getCustomer(customerId),
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
                String customerIdStr = rs.getString("customer_id");
                Customer customer = null;
                if (customerIdStr != null && !customerIdStr.isEmpty()) {
                    customer = customerRepository.getCustomer(UUID.fromString(customerIdStr));
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
                reading.setid(UUID.fromString(rs.getString("id")));
                readings.add(reading);
            }
        }
        return readings;
    }

    // Erlaubt auch ein Update ohne zugeordneten Customer (setzt customer_id auf NULL, wenn kein Customer vorhanden)
    public void updateReading(UUID id, Reading reading) {
        String sql = "UPDATE readings SET customer_id=?, kind_of_meter=?, meter_count=?, date_of_reading=?, meter_id=?, substitute=?, comment=? WHERE id=?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            if (reading.getCustomer() != null && reading.getCustomer().getid() != null) {
                stmt.setString(1, reading.getCustomer().getid().toString());
            } else {
                stmt.setNull(1, Types.CHAR);
            }
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
        // Hier können Sie ggf. die Connection schließen, falls gewünscht.
    }
}
