package model;

import enums.KindOfMeter;
import interfaces.IReading;

import java.time.LocalDate;
import java.util.UUID;

public class Reading implements IReading {

    private String comment;
    private Customer customer;
    private LocalDate dateOfReading;
    private KindOfMeter kindOfMeter;
    private Double meterCount;
    private String meterId;
    private Boolean substitute;
    private UUID uid;

    public Reading(UUID uid, Boolean substitute, String meterId, Double meterCount, KindOfMeter kindOfMeter, LocalDate dateOfReading, Customer customer, String comment) {
<<<<<<< HEAD
        this.uid = uid; // Set the UUID
        this.substitute = substitute;
        this.meterId = meterId;
        this.meterCount = meterCount;
        this.kindOfMeter = kindOfMeter;
        this.dateOfReading = dateOfReading;
        this.customer = customer;
        this.comment = comment;
    }

    public Reading(Boolean substitute, String meterId, Double meterCount, KindOfMeter kindOfMeter, LocalDate dateOfReading, Customer customer, String comment) {
=======
        this.uid = uid; // UUID generieren
>>>>>>> 8ad9d8f5a6c457b465869192ffeca7d20b22b40a
        this.substitute = substitute;
        this.meterId = meterId;
        this.meterCount = meterCount;
        this.kindOfMeter = kindOfMeter;
        this.dateOfReading = dateOfReading;
        this.customer = customer;
        this.comment = comment;
    }


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

    @Override
    public UUID getid() {
        return this.uid;
    }

    @Override
    public void setid(UUID id) {
        this.uid = id;
    }
}