package com.myMoneyTracker.dto.expense;

import java.sql.Timestamp;

import com.myMoneyTracker.dto.category.CategoryDTO;

public class ExpenseDTO {
    
    private long id;
    private CategoryDTO category;
    private String name;
    private String description;
    private Double amount;
    private Timestamp creationDate;
    private String currency;
    private String frequency;
    

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public CategoryDTO getCategory() {

        return category;
    }

    public void setCategory(CategoryDTO category) {

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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
