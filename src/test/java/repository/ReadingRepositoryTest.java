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
    private UUID readingID;
    private UUID customerID;

    @BeforeEach
    void setUp() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");

        this.readingRepository = new ReadingRepository(properties);
        this.customerRepository = new CustomerRepository(properties);

        // Create a customer and a reading to use for the tests
        Customer customer = new Customer(UUID.randomUUID(), "Test", "Customer", LocalDate.of(1990, 1, 1), Gender.M);
        this.customerID = this.customerRepository.createCustomer(customer);

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
        Customer customer = new Customer(UUID.randomUUID(), "Anna", "Schmidt", LocalDate.of(1985, 5, 15), Gender.W);
        this.customerID = this.customerRepository.createCustomer(customer);

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
        Reading fetchedReading = this.readingRepository.getReading(newReadingId);

        assert fetchedReading != null;
        assert fetchedReading.getMeterId().equals("Meter456");
        assert fetchedReading.getCustomer().getFirstName().equals("Anna");
    }

    @Test
    void getReading() {
        Reading reading = this.readingRepository.getReading(this.readingID);
        assert reading != null;
        assert reading.getMeterId().equals("Meter123");
        assert reading.getCustomer().getFirstName().equals("Test");
    }

    @Test
    void updateReading() {
        Reading reading = this.readingRepository.getReading(this.readingID);
        assert reading != null;

        reading.setMeterCount(150.0);
        reading.setComment("Aktualisierte Ablesung");
        this.readingRepository.updateReading(this.readingID, reading);

        Reading updatedReading = this.readingRepository.getReading(this.readingID);
        assert updatedReading != null;
        assert updatedReading.getMeterCount().equals(150.0);
        assert updatedReading.getComment().equals("Aktualisierte Ablesung");
    }

    @Test
    void deleteReading() {
        this.readingRepository.deleteReading(this.readingID);

        Reading reading = this.readingRepository.getReading(this.readingID);
        assert reading == null;
    }
}
