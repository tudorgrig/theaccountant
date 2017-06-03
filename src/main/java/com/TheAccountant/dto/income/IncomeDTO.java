package com.TheAccountant.dto.income;

import com.TheAccountant.dto.abstracts.CurrencyHolderDTO;

import java.sql.Timestamp;

/**
 * DTO class for income model class
 * @author Tudor
 */
public class IncomeDTO extends CurrencyHolderDTO {

    private long id;

    private String name;

    private String description;

    private String frequency;

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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

}
