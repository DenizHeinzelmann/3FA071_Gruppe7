package repository;

import enums.Gender;
import model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

class CustomerRepositoryTest {

    private CustomerRepository customerRepository;
    private UUID customerID = UUID.randomUUID();

    @BeforeEach
    void setUp() throws SQLException {
        System.getProperties().setProperty("Service.db.url", "jdbc:mariadb://localhost:3306/hausfix_db");
        System.getProperties().setProperty("Service.db.user", "root");
        System.getProperties().setProperty("Service.db.pw", "hausverwaltung");

        DatabaseConnection.getInstance().openConnection(System.getProperties());
        this.customerRepository = new CustomerRepository();
    }


    @Test
    void createCustomer() {
        Customer customer = new Customer(this.customerID, "Hans", "Müller", LocalDate.now(), Gender.M);
        this.customerRepository.createCustomer(customer);

        Customer fetchedCustomer = this.customerRepository.getCustomer(this.customerID);
        assert fetchedCustomer != null;
        assert fetchedCustomer.getFirstName().equals("Hans");
        assert fetchedCustomer.getLastName().equals("Müller");
    }

    @Test
    void getCustomer() {
        Customer customer = new Customer(this.customerID, "Anna", "Müller", LocalDate.now(), Gender.W);
        this.customerRepository.createCustomer(customer);

        Customer fetchedCustomer = this.customerRepository.getCustomer(this.customerID);
        assert fetchedCustomer != null;
        assert fetchedCustomer.getFirstName().equals("Anna");
        assert fetchedCustomer.getLastName().equals("Müller");
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
