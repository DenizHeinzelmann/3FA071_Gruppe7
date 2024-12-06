package repository;

import enums.Gender;
import model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

class CustomerRepositoryTest {

    private CustomerRepository customerRepository;
    private UUID customerID = UUID.randomUUID();

    @BeforeEach
    void setUp() throws SQLException, IOException {
        // Load properties from the database.properties file
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new FileNotFoundException("'database.properties' not found!");
            }
            properties.load(input);
        }

        // Open the database connection
        DatabaseConnection.getInstance().openConnection(properties);

        // Initialize the repository
        this.customerRepository = new CustomerRepository();
    }


    @Test
    void createCustomer() {
        Customer customer = new Customer(this.customerID, "Deniz", "Heinzelmann", LocalDate.now(), Gender.M);
        this.customerRepository.createCustomer(customer);

        Customer fetchedCustomer = this.customerRepository.getCustomer(this.customerID);
        assert fetchedCustomer != null;
        assert fetchedCustomer.getFirstName().equals("Deniz");
        assert fetchedCustomer.getLastName().equals("Heinzelmann");
    }

    @Test
    void getCustomer() {
        Customer customer = new Customer(this.customerID, "William", "Shakespeare", LocalDate.now(), Gender.M);
        this.customerRepository.createCustomer(customer);

        Customer fetchedCustomer = this.customerRepository.getCustomer(this.customerID);
        assert fetchedCustomer != null;
        assert fetchedCustomer.getFirstName().equals("William");
        assert fetchedCustomer.getLastName().equals("Shakespeare");
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(this.customerID, "Anna", "Müller", LocalDate.now(), Gender.W);
        this.customerRepository.createCustomer(customer);

        Customer updatedCustomer = new Customer("Anna", "Schmidt", LocalDate.of(1985, 6, 15), Gender.W);
        this.customerRepository.updateCustomer(this.customerID, updatedCustomer);

        Customer fetchedCustomer = this.customerRepository.getCustomer(this.customerID);
        assert fetchedCustomer != null;
        assert fetchedCustomer.getLastName().equals("Schmidt");
        assert fetchedCustomer.getBirthDate().equals(LocalDate.of(1985, 6, 15));
    }

    @Test
    void deleteCustomer() {
        Customer customer = new Customer(this.customerID, "Anna", "Müller", LocalDate.now(), Gender.W);
        this.customerRepository.createCustomer(customer);

        this.customerRepository.deleteCustomer(this.customerID);

        Customer deletedCustomer = this.customerRepository.getCustomer(this.customerID);
        assert deletedCustomer == null;
    }
}
