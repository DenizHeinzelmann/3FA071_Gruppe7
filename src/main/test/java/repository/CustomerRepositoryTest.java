package main.test.java.repository;

import enums.Gender;
import model.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.CustomerRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;

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
        Customer customer = new Customer("Steve", "Müller", LocalDate.now(), Gender.M);
        this.customerRepository.createCustomer(customer);
    }

    @Test
    void getCustomerByID() {
        this.customerRepository.getCustomerByID(1);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer("Gary", "Müller", LocalDate.now(), Gender.W);
        this.customerRepository.updateCustomer(1,customer);
    }

    @Test
    void deleteCustomer() {
        this.customerRepository.deleteCustomer(1);
    }
}