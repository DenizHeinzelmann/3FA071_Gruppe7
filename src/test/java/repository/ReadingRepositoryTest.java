package repository;

import enums.Gender;
import enums.KindOfMeter;
import model.Customer;
import model.Reading;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ReadingRepositoryTest {
    private Properties properties;
    private ReadingRepository readingRepository;
    private CustomerRepository customerRepository;
    private Customer testCustomer;

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");
        readingRepository = new ReadingRepository(properties);
        customerRepository = new CustomerRepository(properties);

        testCustomer = new Customer("Steve", "Müller", LocalDate.of(2024, 10, 18), Gender.M);
        customerRepository.createCustomer(testCustomer);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (testCustomer != null) {
            readingRepository.deleteReading(testCustomer.getid()); // Lösche alle Readings für den Kunden
            customerRepository.deleteCustomer(testCustomer.getid()); // Lösche den Kunden
        }
        readingRepository.close();
    }

    @Test
    public void testCreateReading() {
        Reading reading = new Reading(UUID.randomUUID(), false, "meter_id", 150.0, KindOfMeter.WASSER, LocalDate.now(), testCustomer, "No comment");
        readingRepository.createReading(reading);

        Reading retrievedReading = readingRepository.getReading(reading.getid());
        assertNotNull(retrievedReading, "The created reading should not be null.");
        assertEquals(reading.getMeterId(), retrievedReading.getMeterId(), "The meter ID should match.");
    }
}
