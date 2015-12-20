package com.myMoneyTracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myMoneyTracker.model.expense.Expense;

/**
 * Data access object class for 'expense'
 * 
 * @author Florin, on 20.12.2015
 */
public interface ExpenseDao extends JpaRepository<Expense, Long> {

}
