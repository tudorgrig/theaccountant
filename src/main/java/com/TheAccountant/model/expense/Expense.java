package com.TheAccountant.model.expense;

import com.TheAccountant.model.abstracts.CurrencyHolderEntity;
import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.user.AppUser;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Entity class for the 'expense' table
 *
 * @author Florin, on 20.12.2015
 */
@Entity
public class Expense extends CurrencyHolderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userId")
    private AppUser user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

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

    public Category getCategory() {

        return category;
    }

    public void setCategory(Category category) {

        this.category = category;
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
    public Expense clone(){
        Expense expense = new Expense();
        expense.setCurrency(getCurrency());
        expense.setName(getName());
        expense.setAmount(getAmount());
        expense.setCategory(getCategory());
        expense.setCreationDate(getCreationDate());
        expense.setDescription(getDescription());
        expense.setUser(getUser());
        expense.setFrequency(0);
        expense.setDefaultCurrency(getDefaultCurrency());
        expense.setDefaultCurrencyAmount(getDefaultCurrencyAmount());
        return expense;
    }
}
