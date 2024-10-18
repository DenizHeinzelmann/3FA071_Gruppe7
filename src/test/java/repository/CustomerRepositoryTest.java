package repository;

import enums.Gender;
import model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

class CustomerRepositoryTest {

    private repository.CustomerRepository customerRepository;
    private UUID customerID = UUID.randomUUID();

    @BeforeEach
    void setUp() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(System.getProperty("user.name") + ".db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        properties.setProperty(System.getProperty("user.name") + ".db.user", "root");
        properties.setProperty(System.getProperty("user.name") + ".db.pw", "hausverwaltung");
        this.customerRepository = new repository.CustomerRepository(properties);
    }


    @Test
    void createCustomer() {
        Customer customer = new Customer(this.customerID,"Steve", "Müller", LocalDate.now(), Gender.M);
        this.customerRepository.createCustomer(customer);
    }

    @Test
    void getCustomer() {
        this.customerRepository.getCustomer(this.customerID);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer("Gary", "Müller", LocalDate.now(), Gender.W);
        this.customerRepository.updateCustomer(this.customerID,customer);
    }

    @Test
    void deleteCustomer() {
        this.customerRepository.deleteCustomer(this.customerID);
    }
}