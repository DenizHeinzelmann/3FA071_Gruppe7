package repository;

import enums.Gender;
import enums.KindOfMeter;
import model.Customer;
import model.Reading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

class ReadingRepositoryTest {

    private ReadingRepository readingRepository;
    private CustomerRepository customerRepository;
    private UUID readingID; // This will hold the ID of the reading created in @BeforeEach
    private UUID customerID; // This will hold the ID of the customer created in @BeforeEach

    @BeforeEach
    void setUp() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");

        this.readingRepository = new ReadingRepository(properties);
        this.customerRepository = new CustomerRepository(properties);

        // Create a shared customer for use in all tests
        Customer customer = new Customer(UUID.randomUUID(), "Shared", "Customer", LocalDate.of(1990, 1, 1), Gender.M);
        this.customerID = this.customerRepository.createCustomer(customer);
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
    }

    @Test
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
}