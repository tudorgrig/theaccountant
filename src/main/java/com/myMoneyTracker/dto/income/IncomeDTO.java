package com.myMoneyTracker.dto.income;

import java.sql.Timestamp;

/**
 * DTO class for income model class
 * @author Floryn
 */
public class IncomeDTO {

    private long id;

    private String name;

    private String description;

    private Double amount;

    private Timestamp creationDate;

    private String currency;

    private String frequency;

    private String defaultCurrency;

    private Double defaultCurrencyAmount;

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public Double getAmount() {

        return amount;
    }

    public void setAmount(Double amount) {

        this.amount = amount;
    }

    public Timestamp getCreationDate() {

        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {

        this.creationDate = creationDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public Double getDefaultCurrencyAmount() {
        return defaultCurrencyAmount;
    }

    public void setDefaultCurrencyAmount(Double defaultCurrencyAmount) {
        this.defaultCurrencyAmount = defaultCurrencyAmount;
    }
}
