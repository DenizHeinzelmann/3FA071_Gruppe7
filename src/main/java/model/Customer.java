package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import enums.Gender;
import interfaces.ICustomer;

import java.time.LocalDate;
import java.util.UUID;

public class Customer implements ICustomer {
    @JsonProperty(required = true)
    private String lastname;
    @JsonProperty(required = true)
    private String firstName;
    private LocalDate birthdate;
    @JsonProperty(required = true)
    private Gender gender;
    private UUID id;

    public Customer() {
    }

    public Customer(String firstname, String lastname, LocalDate birthdate, Gender gender) {
        this.lastname = lastname;
        this.firstName = firstname;
        this.birthdate = birthdate;
        this.gender = gender;
    }

    public Customer(UUID uid, String firstname, String lastname, LocalDate birthdate, Gender gender) {
        this.id = uid;
        this.lastname = lastname;
        this.firstName = firstname;
        this.birthdate = birthdate;
        this.gender = gender;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastname = lastName;
    }

    @Override
    public void setBirthDate(LocalDate birthDate) {
        this.birthdate = birthDate;
    }

    @Override
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getLastName() {
        return this.lastname;
    }

    @Override
    public LocalDate getBirthDate() {
        return this.birthdate;
    }

    @Override
    public Gender getGender() {
        return this.gender;
    }

    @Override
    public UUID getid() { // Nicht konform
        return this.id;
    }

    @Override
    public void setid(UUID id) { // Nicht konform
        this.id = id;
    }
}
