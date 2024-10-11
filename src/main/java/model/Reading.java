package model;

import enums.KindOfMeter;
import interfaces.ICustomer;
import interfaces.IReading;

import java.time.LocalDate;

public class Reading implements IReading {

    private String comment;
    private Customer customer;
    private LocalDate dateOfReading;
    private KindOfMeter kindOfMeter;
    private Double meterCount;
    private String meterId;
    private Boolean substitute;


    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void setDateOfReading(LocalDate dateOfReading) {
        this.dateOfReading = dateOfReading;
    }

    @Override
    public void setKindOfMeter(KindOfMeter kindOfMeter) {
        this.kindOfMeter = kindOfMeter;
    }

    @Override
    public void setMeterCount(Double meterCount) {
        this.meterCount = meterCount;
    }

    @Override
    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    @Override
    public void setSubstitute(Boolean substitute) {
        this.substitute = substitute;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public LocalDate getDateOfReading() {
        return this.dateOfReading;
    }

    @Override
    public KindOfMeter getKindOfMeter() {
        return this.kindOfMeter;
    }

    @Override
    public Double getMeterCount() {
        return this.meterCount;
    }

    @Override
    public String getMeterId() {
        return this.meterId;
    }

    @Override
    public Boolean getSubstitute() {
        return this.substitute;
    }

    @Override
    public String printDateOfReading() {
        return this.dateOfReading.toString();
    }
}
