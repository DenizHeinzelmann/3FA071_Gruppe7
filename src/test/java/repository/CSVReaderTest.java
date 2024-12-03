package repository;

import enums.Gender;
import enums.KindOfMeter;
import model.Customer;
import model.Reading;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

public class CSVReaderTest {

    private ReadingRepository readingRepository;
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");

        this.readingRepository = new ReadingRepository(properties);
        this.customerRepository = new CustomerRepository(properties);
    }

    @Test
    void importDataFromCSV() throws IOException {
        List<Customer> customers = importCustomersFromFile("data/customer.csv");
        importReadingsFromFile("data/heating.csv", customers, KindOfMeter.HEIZUNG);
        importReadingsFromFile("data/water.csv", customers, KindOfMeter.WASSER);
        importReadingsFromFile("data/electricity.csv", customers, KindOfMeter.STROM);
    }

    private List<Customer> importCustomersFromFile(String fileName) throws IOException {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                getClass().getClassLoader().getResourceAsStream(fileName)
                        )
                ))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Überspringe die Headerzeile
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length < 4) continue;

                UUID id = UUID.fromString(fields[0]);
                Gender gender = fields[1].equalsIgnoreCase("herr") ? Gender.M : Gender.W;
                String firstName = fields[2];
                String lastName = fields[3];
                LocalDate birthDate = fields.length > 4 && !fields[4].isEmpty()
                        ? LocalDate.parse(fields[4], DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        : null;

                Customer customer = new Customer(id, firstName, lastName, birthDate, gender);
                customerRepository.createCustomer(customer);
                customers.add(customer);
                System.out.println("Kunde gespeichert: " + customer.getid());
            }
        }
        return customers;
    }


    private void importReadingsFromFile(String fileName, List<Customer> customers, KindOfMeter kindOfMeter) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                getClass().getClassLoader().getResourceAsStream(fileName),
                                "Konnte '" + fileName + "' nicht finden!")))) {

            String line;
            String customerUid = null;
            String meterId = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("\"Kunde\"")) {
                    customerUid = line.split(";")[1].replace("\"", "").trim();
                } else if (line.startsWith("\"Zählernummer\"")) {
                    meterId = line.split(";")[1].replace("\"", "").trim();
                } else if (line.matches("^\\d{2}\\.\\d{2}\\.\\d{4}.*")) {
                    String[] fields = line.split(";");
                    LocalDate date = null;

                    try {
                        date = LocalDate.parse(fields[0], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    } catch (DateTimeParseException e) {
                        System.err.println("Ungültiges Datum in Zeile: " + line);
                        continue;
                    }

                    Double meterCount = fields[1].replace(",", ".").isEmpty() ? null : Double.parseDouble(fields[1].replace(",", "."));
                    String comment = fields.length > 2 ? fields[2] : null;

                    final String currentCustomerUid = customerUid; // Effektiv final
                    Customer customer = customers.stream()
                            .filter(c -> c.getid().toString().equals(currentCustomerUid))
                            .findFirst()
                            .orElse(null);

                    if (customer == null) {
                        System.err.println("Kein Kunde mit UUID " + customerUid + " gefunden. Messwert wird übersprungen.");
                        continue;
                    }

                    Reading reading = new Reading(
                            UUID.randomUUID(),
                            false,
                            meterId,
                            meterCount,
                            kindOfMeter,
                            date,
                            customer,
                            comment
                    );

                    this.readingRepository.createReading(reading);
                }
            }
        }
    }
}

