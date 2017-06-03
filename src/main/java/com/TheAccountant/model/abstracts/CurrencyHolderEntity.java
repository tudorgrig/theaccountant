package com.TheAccountant.model.abstracts;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Abstract entity that should be extended by entities that will contain amounts based
 * on a currency.
 *
 * Created by Florin on 5/20/2017.
 */
@MappedSuperclass
public abstract class CurrencyHolderEntity {

    @NotNull
    @Min(value = 0)
    protected Double amount;

    @NotNull
    protected String currency;

    protected String defaultCurrency;
    protected Double defaultCurrencyAmount;

    @NotNull
    private Timestamp creationDate;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }
}
