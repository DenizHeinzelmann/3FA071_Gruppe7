package interfaces;

import enums.KindOfMeter;
import model.Customer;

import java.time.LocalDate;

public interface IReading extends IID {
    void setComment(String comment);
    void setCustomer(Customer customer);
    void setDateOfReading(LocalDate dateOfReading);
    void setKindOfMeter(KindOfMeter kindOfMeter);
    void setMeterCount(Double meterCount);
    void setMeterId(String meterId);
    void setSubstitute(Boolean substitute);
    String getComment();
    Customer getCustomer();
    LocalDate getDateOfReading();
    KindOfMeter getKindOfMeter();
    Double getMeterCount();
    String getMeterId();
    Boolean getSubstitute();
    String printDateOfReading();
}
