package repository;

import enums.Gender;
import enums.KindOfMeter;
import model.Customer;
import model.Reading;
<<<<<<< HEAD
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

=======
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

<<<<<<< HEAD
class ReadingRepositoryTest {

    private ReadingRepository readingRepository;
    private CustomerRepository customerRepository;
    private UUID readingID; // This will hold the ID of the reading created in @BeforeEach
    private UUID customerID; // This will hold the ID of the customer created in @BeforeEach

    @BeforeEach
    void setUp() throws SQLException {
=======
import static org.junit.jupiter.api.Assertions.*;

public class ReadingRepositoryTest {
    private Properties properties;
    private ReadingRepository readingRepository;
    private CustomerRepository customerRepository;
    private Customer testCustomer;

    @BeforeEach
    public void setUp() throws SQLException, IOException {
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");
<<<<<<< HEAD

        this.readingRepository = new ReadingRepository(properties);
        this.customerRepository = new CustomerRepository(properties);

        // Create a new customer
        Customer customer = new Customer(UUID.randomUUID(), "Test", "Customer", LocalDate.of(1990, 1, 1), Gender.M);
        this.customerID = this.customerRepository.createCustomer(customer);

        // Create a new reading associated with the customer
        Reading reading = new Reading(
                UUID.randomUUID(),
                true,
                "Meter123",
                123.45,
                KindOfMeter.WASSER,
                LocalDate.now(),
                customer,
                "Initial Reading"
        );
        this.readingID = this.readingRepository.createReading(reading);
    }

    @Test
    void createReading() {
        // Create a new customer
        Customer customer = new Customer(UUID.randomUUID(), "Anna", "Schmidt", LocalDate.of(1985, 5, 15), Gender.W);
        UUID newCustomerId = this.customerRepository.createCustomer(customer);

        // Create a new reading
        Reading reading = new Reading(
                UUID.randomUUID(),
                true,
                "Meter456",
                200.75,
                KindOfMeter.WASSER,
                LocalDate.now(),
                customer,
                "Test Ablesung"
        );

        UUID newReadingId = this.readingRepository.createReading(reading);

        // Fetch and verify the created reading
        Reading fetchedReading = this.readingRepository.getReading(newReadingId);

        assert fetchedReading != null;
        assert fetchedReading.getMeterId().equals("Meter456");
        assert fetchedReading.getCustomer().getFirstName().equals("Anna");
=======
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
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
    }


    @Test
<<<<<<< HEAD
    void getReading() {
        // Create a reading specifically for this test
        Reading reading = new Reading(
                UUID.randomUUID(),
                false,
                "Meter123",
                123.45,
                KindOfMeter.STROM,
                LocalDate.of(2024, 1, 1),
                this.customerRepository.getCustomer(this.customerID),
                "Get Test Reading"
        );
        UUID readingID = this.readingRepository.createReading(reading);

        // Fetch and verify the created reading
        Reading fetchedReading = this.readingRepository.getReading(readingID);

        assert fetchedReading != null;
        assert fetchedReading.getMeterId().equals("Meter123");
        assert fetchedReading.getComment().equals("Get Test Reading");
    }
    @Test
    void updateReading() {
        // Create a reading specifically for this test
        Reading originalReading = new Reading(
                UUID.randomUUID(),
                true,
                "MeterOriginal",
                300.00,
                KindOfMeter.WASSER,
                LocalDate.of(2023, 11, 1),
                this.customerRepository.getCustomer(this.customerID),
                "Original Reading"
        );
        UUID readingID = this.readingRepository.createReading(originalReading);

        // Fetch and verify the original reading
        Reading fetchedReading = this.readingRepository.getReading(readingID);
        assert fetchedReading != null;
        assert fetchedReading.getMeterId().equals("MeterOriginal");
        assert fetchedReading.getMeterCount().equals(300.00);
        assert fetchedReading.getComment().equals("Original Reading");

        // Update the reading's values
        fetchedReading.setMeterId("MeterUpdated");
        fetchedReading.setMeterCount(500.00);
        fetchedReading.setKindOfMeter(KindOfMeter.STROM);
        fetchedReading.setComment("Updated Reading");
        fetchedReading.setSubstitute(false);
        fetchedReading.setDateOfReading(LocalDate.of(2024, 1, 1));

        // Save the updates
        this.readingRepository.updateReading(readingID, fetchedReading);

        // Fetch and verify the updated reading
        Reading updatedReading = this.readingRepository.getReading(readingID);
        assert updatedReading != null;
        assert updatedReading.getMeterId().equals("MeterUpdated");
        assert updatedReading.getMeterCount().equals(500.00);
        assert updatedReading.getKindOfMeter() == KindOfMeter.STROM;
        assert updatedReading.getComment().equals("Updated Reading");
        assert !updatedReading.getSubstitute();
        assert updatedReading.getDateOfReading().equals(LocalDate.of(2024, 1, 1));
    }


    @Test
    void deleteReading() {
        // Create a reading specifically for this test
        Reading reading = new Reading(
                UUID.randomUUID(),
                false,
                "Meter789",
                150.00,
                KindOfMeter.HEIZUNG,
                LocalDate.of(2023, 12, 31),
                this.customerRepository.getCustomer(this.customerID),
                "Delete Test Reading"
        );
        UUID readingID = this.readingRepository.createReading(reading);

        // Delete the created reading
        this.readingRepository.deleteReading(readingID);

        // Verify that the reading is deleted
        Reading deletedReading = this.readingRepository.getReading(readingID);
        assert deletedReading == null;
}

    @Test
    void deleteReadingById() {
        Reading reading = new Reading(
                UUID.randomUUID(),
                false,
                "Meter789",
                300.00,
                KindOfMeter.HEIZUNG,
                LocalDate.now(),
                new Customer(this.customerID, "Test", "Customer", LocalDate.of(1990, 1, 1), Gender.M),
                "Reading to delete"
        );
        UUID readingIdToDelete = this.readingRepository.createReading(reading);

        this.readingRepository.deleteReadingById(readingIdToDelete);

        // Verify that the reading is deleted
        Reading deletedReading = this.readingRepository.getReading(readingIdToDelete);
        assert deletedReading == null;
    }
=======
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


>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
}