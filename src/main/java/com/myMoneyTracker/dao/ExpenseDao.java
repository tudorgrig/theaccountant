package com.myMoneyTracker.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.myMoneyTracker.model.expense.Expense;

/**
 * Data access object class for 'expense'
 *
 * @author Florin, on 20.12.2015
 */
@Transactional
public interface ExpenseDao extends JpaRepository<Expense, Long> {
    
    @Query("SELECT e FROM Expense e WHERE e.user.username = ?1")
    List<Expense> findByUsername(String username);
    
    @Modifying
    @Query(value = "DELETE FROM expense WHERE user_id IN (SELECT app_user.id FROM app_user WHERE app_user.username= ?1)", nativeQuery = true)
    void deleteAllByUsername(String username);
}
