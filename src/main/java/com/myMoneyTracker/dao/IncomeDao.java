package com.myMoneyTracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myMoneyTracker.model.income.Income;

import java.util.List;

/**
 * Data access object class for 'income'
 * 
 * @author Florin, on 19.12.2015
 */
public interface IncomeDao  extends JpaRepository<Income, Long> {

    List<Income> findByUserId(Long userId);

    List<Income> findByCategoryId(Long categoryId);

    List<Income> findBySubcategoryId(Long subcategoryId);

}
