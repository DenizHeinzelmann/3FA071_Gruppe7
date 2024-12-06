package repository;

import enums.Gender;
import enums.KindOfMeter;
import model.Customer;
import model.Reading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

class ReadingRepositoryTest {

    private ReadingRepository readingRepository;
    private CustomerRepository customerRepository;
    private UUID customerID;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        // Lade die Properties aus einer Datei
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new FileNotFoundException("Die Datei 'database.properties' wurde nicht gefunden!");
            }
            properties.load(input);
        }

        // Verbindung öffnen
        DatabaseConnection.getInstance().openConnection(properties);
        this.customerRepository = new CustomerRepository();
        this.readingRepository = new ReadingRepository();

        // Gemeinsamen Kunden erstellen
        Customer customer = new Customer(UUID.randomUUID(), "Shared", "Customer", LocalDate.of(1990, 1, 1), Gender.M);
        this.customerID = this.customerRepository.createCustomer(customer);
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
                this.customerRepository.getCustomer(this.customerID),
                "Initial Reading"
        );
        UUID readingID = this.readingRepository.createReading(reading);

        Reading fetchedReading = this.readingRepository.getReading(readingID);
        assert fetchedReading != null;
        assert fetchedReading.getMeterId().equals("Meter001");
        assert fetchedReading.getMeterCount().equals(250.00);
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
                this.customerRepository.getCustomer(this.customerID),
                "Test Reading"
        );
        UUID readingID = this.readingRepository.createReading(reading);

        Reading fetchedReading = this.readingRepository.getReading(readingID);
        assert fetchedReading != null;
        assert fetchedReading.getMeterId().equals("Meter002");
        assert fetchedReading.getComment().equals("Test Reading");
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
                this.customerRepository.getCustomer(this.customerID),
                "To be updated"
        );
        UUID readingID = this.readingRepository.createReading(reading);

        Reading updatedReading = new Reading(
                false,
                "MeterUpdated",
                500.00,
                KindOfMeter.WASSER,
                LocalDate.of(2024, 1, 1),
                this.customerRepository.getCustomer(this.customerID),
                "Updated Reading"
        );
        this.readingRepository.updateReading(readingID, updatedReading);

        Reading fetchedReading = this.readingRepository.getReading(readingID);
        assert fetchedReading != null;
        assert fetchedReading.getMeterId().equals("MeterUpdated");
        assert fetchedReading.getMeterCount().equals(500.00);
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
                this.customerRepository.getCustomer(this.customerID),
                "To be deleted"
        );
        UUID readingID = this.readingRepository.createReading(reading);

        this.readingRepository.deleteReading(readingID);

        Reading deletedReading = this.readingRepository.getReading(readingID);
        assert deletedReading == null;
    }
}
