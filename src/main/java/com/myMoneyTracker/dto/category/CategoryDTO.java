package com.myMoneyTracker.dto.category;


/**
 * DTO - data transfer object for category
 * 
 * @author Florin
 */
public class CategoryDTO {

    private long id;

    private String name;

    private String colour;
    
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

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
