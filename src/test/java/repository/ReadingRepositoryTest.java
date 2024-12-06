package repository;

import enums.Gender;
import enums.KindOfMeter;
import model.Customer;
import model.Reading;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReadingRepositoryTest {

    private static ReadingRepository readingRepository;
    private static CustomerRepository customerRepository;
    private UUID customerID;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException, IOException {
        Properties properties = new Properties();
        try (InputStream input = ReadingRepositoryTest.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new FileNotFoundException("'database.properties' not found!");
            }
            properties.load(input);
        }

        DatabaseConnection.getInstance().openConnection(properties);
        customerRepository = new CustomerRepository();
        readingRepository = new ReadingRepository();
    }

    @BeforeEach
    void setUp() throws SQLException {
        Customer customer = new Customer(UUID.randomUUID(), "Shared", "Customer", LocalDate.of(1990, 1, 1), Gender.M);
        this.customerID = customerRepository.createCustomer(customer);
    }

    @Test
    void createReading() {
        Reading reading = new Reading(
                UUID.randomUUID(),
                true,
                "Meter001",
                250.00,
                KindOfMeter.WASSER,
                LocalDate.now(),
                customerRepository.getCustomer(customerID),
                "Initial Reading"
        );
        UUID readingID = readingRepository.createReading(reading);

        Reading fetchedReading = readingRepository.getReading(readingID);
        assertNotNull(fetchedReading);
        assertEquals("Meter001", fetchedReading.getMeterId());
        assertEquals(250.00, fetchedReading.getMeterCount());
    }

    @Test
    void getReading() {
        Reading reading = new Reading(
                UUID.randomUUID(),
                false,
                "Meter002",
                150.00,
                KindOfMeter.STROM,
                LocalDate.of(2024, 1, 1),
                customerRepository.getCustomer(customerID),
                "Test Reading"
        );
        UUID readingID = readingRepository.createReading(reading);

        Reading fetchedReading = readingRepository.getReading(readingID);
        assertNotNull(fetchedReading);
        assertEquals("Meter002", fetchedReading.getMeterId());
        assertEquals("Test Reading", fetchedReading.getComment());
    }

    @Test
    void updateReading() {
        Reading reading = new Reading(
                UUID.randomUUID(),
                false,
                "Meter003",
                100.00,
                KindOfMeter.HEIZUNG,
                LocalDate.of(2023, 12, 31),
                customerRepository.getCustomer(customerID),
                "To be updated"
        );
        UUID readingID = readingRepository.createReading(reading);

        Reading updatedReading = new Reading(
                false,
                "MeterUpdated",
                500.00,
                KindOfMeter.WASSER,
                LocalDate.of(2024, 1, 1),
                customerRepository.getCustomer(customerID),
                "Updated Reading"
        );
        readingRepository.updateReading(readingID, updatedReading);

        Reading fetchedReading = readingRepository.getReading(readingID);
        assertNotNull(fetchedReading);
        assertEquals("MeterUpdated", fetchedReading.getMeterId());
        assertEquals(500.00, fetchedReading.getMeterCount());
    }

    @Test
    void deleteReading() {
        Reading reading = new Reading(
                UUID.randomUUID(),
                true,
                "Meter004",
                400.00,
                KindOfMeter.HEIZUNG,
                LocalDate.now(),
                customerRepository.getCustomer(customerID),
                "To be deleted"
        );
        UUID readingID = readingRepository.createReading(reading);

        readingRepository.deleteReading(readingID);

        Reading deletedReading = readingRepository.getReading(readingID);
        assertNull(deletedReading);
    }
}
