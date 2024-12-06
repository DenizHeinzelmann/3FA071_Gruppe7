package repository;

import enums.KindOfMeter;
import model.Customer;
import model.Reading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CSVReaderTest {

    private CSVReader csvReader;
    private CustomerRepository customerRepository;
    private ReadingRepository readingRepository;

    @BeforeEach
    void setUp() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new FileNotFoundException("The file 'database.properties' was not found!");
            }
            properties.load(input);
        }

        DatabaseConnection.getInstance().openConnection(properties);
        DatabaseConnection.getInstance().truncateAllTables();

        this.customerRepository = new CustomerRepository();
        this.readingRepository = new ReadingRepository();
        this.csvReader = new CSVReader();
    }

    @Test
    void testImportCustomersFromCSV() throws IOException, SQLException {
        List<Customer> customers = csvReader.importCustomersFromFile("data/customer.csv");
        assertNotNull(customers);
        assertFalse(customers.isEmpty());
        List<Customer> storedCustomers = customerRepository.getAllCustomers();
        assertEquals(customers.size(), storedCustomers.size());
    }

    @Test
    void testImportReadingsFromCSV() throws IOException, SQLException {
        List<Customer> customers = csvReader.importCustomersFromFile("data/customer.csv");
        csvReader.importReadingsFromFile("data/water.csv", customers, KindOfMeter.WASSER);
        csvReader.importReadingsFromFile("data/electricity.csv", customers, KindOfMeter.STROM);
        csvReader.importReadingsFromFile("data/heating.csv", customers, KindOfMeter.HEIZUNG);
        List<Reading> readings = readingRepository.getAllReadings();
        assertNotNull(readings);
        assertFalse(readings.isEmpty());
    }

    @Test
    void testCompleteImportProcess() throws IOException, SQLException {
        csvReader.importDataFromCSV();
        List<Customer> customers = customerRepository.getAllCustomers();
        List<Reading> readings = readingRepository.getAllReadings();
        assertNotNull(customers);
        assertFalse(customers.isEmpty());
        assertNotNull(readings);
        assertFalse(readings.isEmpty());
    }
}