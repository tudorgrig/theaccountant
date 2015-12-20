package com.myMoneyTracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myMoneyTracker.model.income.Income;

/**
 * Data access object class for 'income'
 * 
 * @author Florin, on 19.12.2015
 */
public interface IncomeDao extends JpaRepository<Income, Long> {

}
