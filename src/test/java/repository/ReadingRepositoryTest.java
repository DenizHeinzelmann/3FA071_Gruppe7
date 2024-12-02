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
    private UUID customerID; // ID für den Kunden, der in den Tests verwendet wird

    @BeforeEach
    void setUp() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");

        // Initialisiere die Repositories
        this.customerRepository = new CustomerRepository(properties);
        this.readingRepository = new ReadingRepository(properties);

        // Erstelle einen gemeinsamen Kunden für die Tests
        Customer customer = new Customer(UUID.randomUUID(), "Shared", "Customer", LocalDate.of(1990, 1, 1), Gender.M);
        this.customerID = this.customerRepository.createCustomer(customer);
    }

    @Test
    void createReading() {
        // Erstelle ein Reading für den existierenden Kunden
        Reading reading = new Reading(
                UUID.randomUUID(),
                false,
                "Meter123",
                150.00,
                KindOfMeter.WASSER,
                LocalDate.now(),
                this.customerRepository.getCustomer(this.customerID),
                "Initial Reading Test"
        );

        UUID readingId = this.readingRepository.createReading(reading);

        // Überprüfe, ob das Reading korrekt in der Datenbank gespeichert wurde
        Reading fetchedReading = this.readingRepository.getReading(readingId);

        assert fetchedReading != null;
        assert fetchedReading.getMeterId().equals("Meter123");
        assert fetchedReading.getCustomer().getid().equals(this.customerID);
    }

    @Test
    void getReading() {
        // Erstelle ein Reading
        Reading reading = new Reading(
                UUID.randomUUID(),
                false,
                "Meter456",
                200.75,
                KindOfMeter.STROM,
                LocalDate.of(2024, 1, 1),
                this.customerRepository.getCustomer(this.customerID),
                "Get Test Reading"
        );
        UUID readingId = this.readingRepository.createReading(reading);

        // Hole das Reading aus der Datenbank
        Reading fetchedReading = this.readingRepository.getReading(readingId);

        assert fetchedReading != null;
        assert fetchedReading.getMeterId().equals("Meter456");
        assert fetchedReading.getComment().equals("Get Test Reading");
    }

    @Test
    void updateReading() {
        // Erstelle ein Reading
        Reading reading = new Reading(
                UUID.randomUUID(),
                true,
                "MeterOriginal",
                300.00,
                KindOfMeter.HEIZUNG,
                LocalDate.of(2023, 11, 1),
                this.customerRepository.getCustomer(this.customerID),
                "Original Reading"
        );
        UUID readingId = this.readingRepository.createReading(reading);

        // Aktualisiere das Reading
        reading.setMeterId("MeterUpdated");
        reading.setMeterCount(500.00);
        reading.setKindOfMeter(KindOfMeter.STROM);
        reading.setComment("Updated Reading");
        this.readingRepository.updateReading(readingId, reading);

        // Hole das aktualisierte Reading aus der Datenbank
        Reading updatedReading = this.readingRepository.getReading(readingId);

        assert updatedReading != null;
        assert updatedReading.getMeterId().equals("MeterUpdated");
        assert updatedReading.getMeterCount() == 500.00;
        assert updatedReading.getKindOfMeter() == KindOfMeter.STROM;
        assert updatedReading.getComment().equals("Updated Reading");
    }

    @Test
    void deleteReading() {
        // Erstelle ein Reading
        Reading reading = new Reading(
                UUID.randomUUID(),
                false,
                "Meter789",
                400.00,
                KindOfMeter.WASSER,
                LocalDate.of(2023, 12, 31),
                this.customerRepository.getCustomer(this.customerID),
                "Delete Test Reading"
        );
        UUID readingId = this.readingRepository.createReading(reading);

        // Lösche das Reading
        this.readingRepository.deleteReading(readingId);

        // Stelle sicher, dass das Reading gelöscht wurde
        Reading deletedReading = this.readingRepository.getReading(readingId);
        assert deletedReading == null;
    }
}
