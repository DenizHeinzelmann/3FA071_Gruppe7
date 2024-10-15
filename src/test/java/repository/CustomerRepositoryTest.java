package test.java.repository;

import enums.Gender;
import model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.CustomerRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

class CustomerRepositoryTest {

    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "test");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");
        this.customerRepository = new CustomerRepository(properties);
    }


    @Test
    void createCustomer() {
        Customer customer = new Customer(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"),"Steve", "Müller", LocalDate.now(), Gender.M);
        this.customerRepository.createCustomer(customer);
    }

    @Test
    void getCustomer() {
        this.customerRepository.getCustomer(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer("Gary", "Müller", LocalDate.now(), Gender.W);
        this.customerRepository.updateCustomer(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"),customer);
    }

    @Test
    void deleteCustomer() {
        this.customerRepository.deleteCustomer(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
    }
}