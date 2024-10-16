package interfaces;

import enums.Gender;

import java.time.LocalDate;
import java.util.UUID;

public interface ICustomer extends IID {
    void setFirstName(String firstName);
    void setLastName(String lastName);
    void setBirthDate(LocalDate birthDate);
    void setGender(Gender gender);
    String getFirstName();
    String getLastName();
    LocalDate getBirthDate();
    Gender getGender();

    void setid(UUID id);
}
