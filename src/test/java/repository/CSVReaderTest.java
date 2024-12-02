package repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

public class CSVReaderTest {

    private CSVReader csvReader;

    @BeforeEach
    void setUp() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");

        csvReader = new CSVReader(properties);
    }

    @Test
    void testImportDataFromCSV() {
        try {
            csvReader.importDataFromCSV();
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Der Importprozess ist fehlgeschlagen: " + e.getMessage();
        }
    }
}
