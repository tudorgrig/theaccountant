package com.myMoneyTracker.model.category;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by florinIacob on 18.12.2015.
 * Entity class for the 'category' table
 */
@Entity
public class Category {
	
	@Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
	@NotNull
	@Length(min=3, message="Category name should have at least 3 characters")
	@Column(nullable = false)
	private String name;
	
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
	
	
}
