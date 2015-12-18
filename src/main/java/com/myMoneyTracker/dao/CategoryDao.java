package com.myMoneyTracker.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myMoneyTracker.model.category.Category;

/**
 * Created by florinIacob on 18.12.2015.
 * Data access object class for 'category' 
 */
@Transactional
public interface CategoryDao extends JpaRepository<Category, Long> {

}
