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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

public class CSVReader {

    private final ReadingRepository readingRepository;
    private final CustomerRepository customerRepository;

    public CSVReader(Properties properties) throws Exception {
        this.customerRepository = new CustomerRepository(properties);
        this.readingRepository = new ReadingRepository(properties);
    }

    public void importDataFromCSV() throws IOException {
        System.out.println("Beginne mit dem Import der Daten...");
        List<Customer> customers = importCustomersFromFile("data/customers.csv");
        importReadingsFromFile("data/heating.csv", customers, KindOfMeter.HEIZUNG);
        importReadingsFromFile("data/water.csv", customers, KindOfMeter.WASSER);
        importReadingsFromFile("data/electricity.csv", customers, KindOfMeter.STROM);
        System.out.println("Datenimport abgeschlossen.");
    }

    private List<Customer> importCustomersFromFile(String fileName) throws IOException {
        System.out.println("Importiere Kunden aus Datei: " + fileName);
        List<Customer> customers = new ArrayList<>();

        try (BufferedReader customerReader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                getClass().getClassLoader().getResourceAsStream(fileName),
                                "Konnte Datei '" + fileName + "' nicht finden!")))) {

            String line;
            while ((line = customerReader.readLine()) != null) {
                System.out.println("Zeile gelesen: " + line);
                String[] fields = line.split(",");
                if (fields.length < 4) {
                    System.err.println("Ungültige Zeile in customers.csv: " + line);
                    continue;
                }

                try {
                    UUID id = UUID.fromString(fields[0]);
                    String genderStr = fields[1];
                    String firstName = fields[2];
                    String lastName = fields[3];
                    LocalDate birthDate = null;

                    if (fields.length > 4 && !fields[4].isEmpty()) {
                        try {
                            birthDate = LocalDate.parse(fields[4], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        } catch (DateTimeParseException e) {
                            System.err.println("Ungültiges Geburtsdatum: " + fields[4] + ". Es wird 'null' gesetzt.");
                        }
                    }

                    Gender customerGender = switch (genderStr.toLowerCase()) {
                        case "herr" -> Gender.M;
                        case "frau" -> Gender.W;
                        default -> null;
                    };

                    Customer customer = new Customer(id, firstName, lastName, birthDate, customerGender);
                    this.customerRepository.createCustomer(customer);
                    customers.add(customer);
                    System.out.println("Kunde hinzugefügt: " + customer);

                } catch (IllegalArgumentException e) {
                    System.err.println("Fehler beim Verarbeiten der Zeile: " + line + " - " + e.getMessage());
                }
            }
        }

        System.out.println("Kundenimport abgeschlossen: " + customers.size() + " Kunden importiert.");
        return customers;
    }

    private void importReadingsFromFile(String fileName, List<Customer> customers, KindOfMeter kindOfMeter) throws IOException {
        System.out.println("Importiere Messwerte aus Datei: " + fileName);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                getClass().getClassLoader().getResourceAsStream(fileName),
                                "Konnte Datei '" + fileName + "' nicht finden!")))) {

            String line;
            String customerUid = null;
            String meterId = null;

            while ((line = reader.readLine()) != null) {
                System.out.println("Zeile gelesen: " + line);
                if (line.startsWith("\"Kunde\"")) {
                    customerUid = line.split(";")[1].replace("\"", "").trim();
                    System.out.println("Kundenzuordnung gefunden: " + customerUid);
                } else if (line.startsWith("\"Zählernummer\"")) {
                    meterId = line.split(";")[1].replace("\"", "").trim();
                    System.out.println("Zählernummer gefunden: " + meterId);
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

                    final String currentCustomerUid = customerUid;
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
                    System.out.println("Messwert hinzugefügt: " + reading);
                }
            }
        }

        System.out.println("Messwertimport abgeschlossen: Datei " + fileName);
    }
}
