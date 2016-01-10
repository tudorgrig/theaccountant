package com.myMoneyTracker.model.subcategory;

import com.myMoneyTracker.model.category.Category;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Tudor Grigoriu.
 * This class is the entity class for the subcategory
 */
@Entity
@Table(name = "subcategory")
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id")
    @NotNull
    private Category category;

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
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
}
