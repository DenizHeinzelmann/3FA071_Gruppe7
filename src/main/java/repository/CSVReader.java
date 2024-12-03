package repository;

import enums.Gender;
import enums.KindOfMeter;
import model.Customer;
import model.Reading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

public class CSVReader {

    private final CustomerRepository customerRepository;
    private final ReadingRepository readingRepository;

    public CSVReader(Properties properties) throws Exception {
        this.customerRepository = new CustomerRepository(properties);
        this.readingRepository = new ReadingRepository(properties);
    }

    public void importDataFromCSV() throws IOException {
        System.out.println("Beginne mit dem Import der Daten...");
        List<Customer> customers = importCustomersFromFile("data/customer.csv");
        importReadingsFromFile("data/water.csv", customers, KindOfMeter.WASSER);
        System.out.println("Datenimport abgeschlossen.");
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
                    isHeader = false; // Überspringe die erste Zeile (Header)
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
                                getClass().getClassLoader().getResourceAsStream(fileName)
                        )
                ))) {
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
                    LocalDate date = LocalDate.parse(fields[0], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    Double meterCount = fields[1].isEmpty() ? null : Double.parseDouble(fields[1].replace(",", "."));
                    String comment = fields.length > 2 ? fields[2] : null;

                    UUID customerId = UUID.fromString(customerUid);
                    Customer customer = customerRepository.getCustomer(customerId);
                    if (customer == null) {
                        System.err.println("Kunde mit UUID " + customerUid + " nicht gefunden.");
                        continue;
                    }

                    Reading reading = new Reading(UUID.randomUUID(), false, meterId, meterCount, kindOfMeter, date, customer, comment);
                    readingRepository.createReading(reading);
                }
            }
        }
    }
}
