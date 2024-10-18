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

        // Erstelle einen Testkunden
        testCustomer = new Customer("Steve", "Müller", LocalDate.of(2024, 10, 18), Gender.M);
        customerRepository.createCustomer(testCustomer);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (testCustomer != null) {
            // Delete readings first to avoid foreign key constraint issues
            System.out.println("Deleting readings for customer ID: " + testCustomer.getid());
            readingRepository.deleteReading(testCustomer.getid()); // Ensure this method deletes all readings for the customer
            System.out.println("Deleting customer ID: " + testCustomer.getid());
            customerRepository.deleteCustomer(testCustomer.getid());
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

    @Test
    public void testGetReading() {
        UUID readingId = UUID.randomUUID();
        System.out.println(readingId);
        Reading reading = new Reading(readingId, false, "meter_id", 150.0, KindOfMeter.WASSER, LocalDate.now(), testCustomer, "No comment");

        // Check if the reading was created successfully
        readingRepository.createReading(reading);

        // Retrieve the reading by its ID
        Reading retrievedReading = readingRepository.getReading(readingId);

        // Print debug info
        System.out.println("Retrieved reading: " + retrievedReading);

        assertNotNull(retrievedReading, "The retrieved reading should not be null.");
        assertEquals(readingId, retrievedReading.getid(), "The reading ID should match.");
        assertEquals(reading.getMeterId(), retrievedReading.getMeterId(), "The meter ID should match.");
        assertEquals(reading.getMeterCount(), retrievedReading.getMeterCount(), "The meter count should match.");
        assertEquals(reading.getKindOfMeter(), retrievedReading.getKindOfMeter(), "The kind of meter should match.");
        assertEquals(reading.getDateOfReading(), retrievedReading.getDateOfReading(), "The date of reading should match.");
        assertEquals(reading.getCustomer().getid(), retrievedReading.getCustomer().getid(), "The customer should match.");
        assertEquals(reading.getComment(), retrievedReading.getComment(), "The comment should match.");
    }

    @Test
    public void testDeleteReading() {
        // Erstelle eine Lesung, um sie später zu löschen
        Reading reading = new Reading(UUID.randomUUID(), false, "meter_id", 150.0, KindOfMeter.WASSER, LocalDate.now(), testCustomer, "No comment");
        readingRepository.createReading(reading);

        // Überprüfen, ob die Lesung erfolgreich erstellt wurde
        Reading retrievedReading = readingRepository.getReading(reading.getid());
        assertNotNull(retrievedReading, "The created reading should not be null.");

        // Löschen der Lesung
        readingRepository.deleteReading(reading.getid());

        // Überprüfen, ob die Lesung nach dem Löschen nicht mehr existiert
        Reading deletedReading = readingRepository.getReading(reading.getid());
        assertNull(deletedReading, "The deleted reading should be null.");
    }

    @Test
    public void testUpdateReading() {
        Reading reading = new Reading(UUID.randomUUID(), false, "meter_id", 150.0, KindOfMeter.WASSER, LocalDate.now(), testCustomer, "No comment");
        readingRepository.createReading(reading);

        Reading retrievedReading = readingRepository.getReading(reading.getid());
        assertNotNull(retrievedReading, "The created reading should not be null.");

        retrievedReading.setMeterCount(200.0);
        retrievedReading.setComment("Updated comment");
        retrievedReading.setSubstitute(true);

        readingRepository.updateReading(retrievedReading.getid(), retrievedReading);

        Reading updatedReading = readingRepository.getReading(retrievedReading.getid());
        assertNotNull(updatedReading, "The updated reading should not be null.");
        assertEquals(200.0, updatedReading.getMeterCount(), "The meter count should be updated.");
        assertEquals("Updated comment", updatedReading.getComment(), "The comment should be updated.");
        assertTrue(updatedReading.getSubstitute(), "The substitute value should be updated.");
    }


}