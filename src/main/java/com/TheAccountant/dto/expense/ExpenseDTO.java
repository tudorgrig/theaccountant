package com.TheAccountant.dto.expense;

import com.TheAccountant.dto.abstracts.CurrencyHolderDTO;
import com.TheAccountant.dto.category.CategoryDTO;

import java.sql.Timestamp;

public class ExpenseDTO extends CurrencyHolderDTO {
    
    private long id;
    private CategoryDTO category;
    private String name;
    private String description;
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
