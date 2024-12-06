package repository;

import enums.Gender;
import model.Customer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryTest {

    private static CustomerRepository customerRepository;
    private UUID customerID;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException, IOException {
        Properties properties = new Properties();
        try (InputStream input = CustomerRepositoryTest.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new FileNotFoundException("'database.properties' not found!");
            }
            properties.load(input);
        }

        DatabaseConnection.getInstance().openConnection(properties);
        customerRepository = new CustomerRepository();
    }

    @BeforeEach
    void setUp() {
        this.customerID = UUID.randomUUID();
    }

    @Test
    void createCustomer() {
        Customer customer = new Customer(this.customerID, "Deniz", "Heinzelmann", LocalDate.now(), Gender.M);
        customerRepository.createCustomer(customer);

        Customer fetchedCustomer = customerRepository.getCustomer(this.customerID);
        assertNotNull(fetchedCustomer);
        assertEquals("Deniz", fetchedCustomer.getFirstName());
        assertEquals("Heinzelmann", fetchedCustomer.getLastName());
    }

    @Test
    void getCustomer() {
        Customer customer = new Customer(this.customerID, "William", "Shakespeare", LocalDate.now(), Gender.M);
        customerRepository.createCustomer(customer);

        Customer fetchedCustomer = customerRepository.getCustomer(this.customerID);
        assertNotNull(fetchedCustomer);
        assertEquals("William", fetchedCustomer.getFirstName());
        assertEquals("Shakespeare", fetchedCustomer.getLastName());
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(this.customerID, "Anna", "Müller", LocalDate.now(), Gender.W);
        customerRepository.createCustomer(customer);

        Customer updatedCustomer = new Customer("Anna", "Schmidt", LocalDate.of(1985, 6, 15), Gender.W);
        customerRepository.updateCustomer(this.customerID, updatedCustomer);

        Customer fetchedCustomer = customerRepository.getCustomer(this.customerID);
        assertNotNull(fetchedCustomer);
        assertEquals("Schmidt", fetchedCustomer.getLastName());
        assertEquals(LocalDate.of(1985, 6, 15), fetchedCustomer.getBirthDate());
    }

    @Test
    void deleteCustomer() {
        Customer customer = new Customer(this.customerID, "Anna", "Müller", LocalDate.now(), Gender.W);
        customerRepository.createCustomer(customer);

        customerRepository.deleteCustomer(this.customerID);

        Customer deletedCustomer = customerRepository.getCustomer(this.customerID);
        assertNull(deletedCustomer);
    }
}