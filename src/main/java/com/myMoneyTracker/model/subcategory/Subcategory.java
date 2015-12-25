package com.myMoneyTracker.model.subcategory;

import com.myMoneyTracker.model.category.Category;

import javax.persistence.*;

/**
 * @author Tudor Grigoriu.
 * This class is the entity class for the subcategory
 */
@Entity
@Table(name = "subcategory")
public class Subcategory {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="category_id")
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
}
