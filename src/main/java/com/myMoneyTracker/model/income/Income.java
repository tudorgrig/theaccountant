package com.myMoneyTracker.model.income;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.myMoneyTracker.model.user.AppUser;

/**
 * Entity class for the 'income' table
 *
 * @author Florin, on 19.12.2015
 */
@Entity
public class Income {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    @NotNull
    private AppUser user;
    
    @NotNull
    private String name;
    
    private String description;
    
    @NotNull
    private Double amount;
    
    @NotNull
    private Timestamp creationDate;

    @NotNull
    private String currency;
    
    public long getId() {
    
        return id;
    }
    
    public void setId(long id) {
    
        this.id = id;
    }
    
    public AppUser getUser() {
    
        return user;
    }
    
    public void setUser(AppUser user) {
    
        this.user = user;
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
}
