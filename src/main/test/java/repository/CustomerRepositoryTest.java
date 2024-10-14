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
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");
        this.customerRepository = new CustomerRepository(properties);
    }


    @Test
    void createCustomer() {
        Customer customer = new Customer("Steve", "Müller", LocalDate.now(), Gender.M);
    }

    @Test
    void getCustomerByID() {
        this.customerRepository.getCustomerByID(1);
    }

    @Test
    void updateCustomer() {
    }

    @Test
    void deleteCustomer() {
    }
}