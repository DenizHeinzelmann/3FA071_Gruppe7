package repository;

import enums.Gender;
import enums.KindOfMeter;
import model.Customer;
import model.Reading;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CSVReader {

    private final CustomerRepository customerRepository;
    private final ReadingRepository readingRepository;

    public CSVReader() throws Exception {
        DatabaseConnection.getInstance().openConnection(System.getProperties());
        this.customerRepository = new CustomerRepository();
        this.readingRepository = new ReadingRepository();
    }

    public void importDataFromCSV() throws IOException {
        System.out.println("Starting Data-Import...");
        List<Customer> customers = importCustomersFromFile("data/customer.csv");
        importReadingsFromFile("data/water.csv", customers, KindOfMeter.WASSER);
        importReadingsFromFile("data/electricity.csv", customers, KindOfMeter.STROM);
        importReadingsFromFile("data/heating.csv", customers, KindOfMeter.HEIZUNG);
        System.out.println("Data-Import completed.");
    }

    public List<Customer> importCustomersFromFile(String fileName) throws IOException {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                getClass().getClassLoader().getResourceAsStream(fileName),
                                "File not found: " + fileName
                        )
                ))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip header line
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length < 4) {
                    throw new IOException("Invalid line in customer file: " + line);
                }

                try {
                    UUID id = UUID.fromString(fields[0]);
                    Gender gender = parseGender(fields[1]);
                    String firstName = fields[2];
                    String lastName = fields[3];
                    LocalDate birthDate = parseDate(fields.length > 4 ? fields[4] : null);

                    Customer customer = new Customer(id, firstName, lastName, birthDate, gender);
                    customerRepository.createCustomer(customer);
                    customers.add(customer);
                    System.out.println("Customer saved: " + customer.getid());
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    throw new IOException("Error processing line: " + line + " - " + e.getMessage(), e);
                }
            }
        }
        return customers;
    }

    public void importReadingsFromFile(String fileName, List<Customer> customers, KindOfMeter kindOfMeter) throws IOException {
        BufferedReader reader;

        if (fileName.startsWith("/") || fileName.contains(":")) {
            reader = new BufferedReader(new FileReader(fileName));
        } else {
            InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(fileName);
            if (resourceStream == null) {
                throw new FileNotFoundException("File not found: " + fileName);
            }
            reader = new BufferedReader(new InputStreamReader(resourceStream));
        }

        try (reader) {
            String line;
            String customerUid = null;
            String meterId = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("\"Kunde\"")) {
                    customerUid = extractField(line, 1);
                } else if (line.startsWith("\"Zählernummer\"")) {
                    meterId = extractField(line, 1);
                } else if (line.matches("^\\d{2}\\.\\d{2}\\.\\d{4}.*")) {
                    String[] fields = line.split(";");
                    try {
                        LocalDate date = parseDate(fields[0]);
                        Double meterCount = parseDouble(fields[1]);
                        String comment = fields.length > 2 ? fields[2] : null;

                        if (customerUid == null || customerUid.isEmpty()) {
                            throw new IOException("Customer UUID is missing or invalid in file: " + fileName);
                        }
                        if (meterId == null || meterId.isEmpty()) {
                            throw new IOException("Meter ID is missing or invalid in file: " + fileName);
                        }

                        UUID customerId = UUID.fromString(customerUid);
                        Customer customer = customers.stream()
                                .filter(c -> c.getid().equals(customerId))
                                .findFirst()
                                .orElse(null);

                        if (customer == null) {
                            throw new IOException("No customer with UUID " + customerUid + " found in the provided customers list.");
                        }

                        Reading reading = new Reading(UUID.randomUUID(), false, meterId, meterCount, kindOfMeter, date, customer, comment);
                        readingRepository.createReading(reading);
                    } catch (IllegalArgumentException e) {
                        throw new IOException("Error processing line: " + line + " - " + e.getMessage(), e);
                    }
                }
            }
        }
    }

    private Gender parseGender(String genderStr) {
        return switch (genderStr.toLowerCase()) {
            case "herr" -> Gender.M;
            case "frau" -> Gender.W;
            case "divers" -> Gender.D;
            default -> null;
        };
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private Double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Double.parseDouble(value.replace(",", "."));
    }

    private String extractField(String line, int index) {
        String[] parts = line.split(";");
        if (index >= parts.length) {
            throw new IllegalArgumentException("Invalid index for line: " + line);
        }
        return parts[index].replace("\"", "").trim();
    }
}