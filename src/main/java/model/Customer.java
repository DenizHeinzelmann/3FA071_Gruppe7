package model;

import enums.Gender;
import interfaces.ICustomer;

import java.time.LocalDate;

public class Customer implements ICustomer {
    private String lastname;
    private String firstName;
    private LocalDate birthdate;
    private Gender gender;

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
}
