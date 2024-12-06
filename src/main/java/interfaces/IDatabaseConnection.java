package interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public interface IDatabaseConnection {

    Connection openConnection(Properties properties) throws SQLException;

    void createAllTables() throws SQLException;

    void truncateAllTables() throws SQLException;

    void removeAllTables() throws SQLException;

    void closeConnection() throws SQLException;
}