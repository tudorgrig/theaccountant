package com.myMoneyTracker.model.expense;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.user.AppUser;

/**
 * Entity class for the 'expense' table
 *
 * @author Florin, on 20.12.2015
 */
@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull
    private String name;
    private String description;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Timestamp creationDate;

    @NotNull
    private String currency;

    @Column(name = "start_day", nullable = true)
    private Integer startDay;

    @Column(name = "start_month", nullable = true)
    private Integer startMonth;

    @Column(name = "frequency", nullable = true)
    private String frequency;

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

    public Integer getStartDay() {
        return startDay;
    }

    public void setStartDay(Integer startDay) {
        this.startDay = startDay;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
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
        expense.setStartDay(getStartDay());
        expense.setStartMonth(getStartMonth());
        expense.setFrequency(getFrequency());
        return expense;
    }
}
