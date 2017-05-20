package com.TheAccountant.model.income;

import com.TheAccountant.model.abstracts.CurrencyHolderEntity;
import com.TheAccountant.model.user.AppUser;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Entity class for the 'income' table
 *
 * @author Florin, on 19.12.2015
 */
@Entity
public class Income extends CurrencyHolderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @NotNull
    private String name;

    private String description;

    @Column(name = "frequency", nullable = true)
    private Integer frequency;


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

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    @Override
    public Income clone() {
        Income income = new Income();
        income.setUser(getUser());
        income.setCurrency(getCurrency());
        income.setDescription(getDescription());
        income.setCreationDate(getCreationDate());
        income.setAmount(getAmount());
        income.setDefaultCurrency(getDefaultCurrency());
        income.setDefaultCurrencyAmount(getDefaultCurrencyAmount());
        income.setName(getName());
        income.setFrequency(0);
        return income;
    }
}
