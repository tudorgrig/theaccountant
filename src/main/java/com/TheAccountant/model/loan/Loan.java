package com.TheAccountant.model.loan;

import com.TheAccountant.model.counterparty.Counterparty;
import com.TheAccountant.model.user.AppUser;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Created by Florin on 3/7/2017.
 */
@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "counterparty_id")
    private Counterparty counterparty;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_Id")
    private AppUser user;

    @NotNull
    private Boolean receiving;

    @Column(nullable = false)
    @Min(value = 0)
    private Double amount;

    @Column(nullable = false)
    private Timestamp creationDate;

    @NotNull
    private String currency;

    private Double defaultCurrencyAmount;

    @NotNull
    private Boolean active;

    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Counterparty getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(Counterparty counterparty) {
        this.counterparty = counterparty;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Boolean getReceiving() {
        return receiving;
    }

    public void setReceiving(Boolean receiving) {
        this.receiving = receiving;
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

    public Double getDefaultCurrencyAmount() {
        return defaultCurrencyAmount;
    }

    public void setDefaultCurrencyAmount(Double defaultCurrencyAmount) {
        this.defaultCurrencyAmount = defaultCurrencyAmount;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
