package com.myMoneyTracker.dto.income;

import com.myMoneyTracker.dto.user.AppUserDTO;
import com.myMoneyTracker.model.user.AppUser;

import java.sql.Timestamp;

/**
 * DTO class for income model class
 * @author Floryn
 */
public class IncomeDTO {

    private long id;

    private AppUserDTO user;

    private String name;

    private String description;

    private Double amount;

    private Timestamp creationDate;

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public AppUserDTO getUser() {

        return user;
    }

    public void setUser(AppUserDTO user) {

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
}
