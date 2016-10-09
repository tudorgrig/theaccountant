package com.myMoneyTracker.model.category;

import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.user.AppUser;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by florinIacob on 18.12.2015.
 * Entity class for the 'category' table
 */
@Entity
@Table(name = "category",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "userId"}) })
public class Category {

    private long id;

    private String name;

    private AppUser user;

    private String colour = "stable";

    private Set<Expense> expenses = new HashSet<>();

    private float threshold = 0;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category" , cascade = CascadeType.ALL)
    public Set<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
    }

    public Category(){}

    public Category(String name, String colour){
        this.name = name;
        this.colour = colour;
    }


    @Column(name = "id", unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

    @Column(name = "name", unique = false, nullable = false)
    @NotNull
    @Length(min = 3, message = "Category name should have at least 3 characters")
    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userId")
    public AppUser getUser() {

        return user;
    }

    public void setUser(AppUser user) {

        this.user = user;
    }


    @Column(name = "colour", unique = false)
    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }


    @Column(name = "threshold", unique = false, nullable = true)
    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }


}
