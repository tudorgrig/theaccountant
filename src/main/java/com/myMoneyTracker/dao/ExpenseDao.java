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
    
    @Query("SELECT e FROM Expense e WHERE e.category.name = ?1 AND e.user.username = ?2")
    List<Expense> findByCategoryNameAndUsername(String categoryName, String username);
    
    @Modifying
    @Query(value = "DELETE FROM expense WHERE user_id IN (SELECT app_user.id FROM app_user WHERE app_user.username= ?1)", nativeQuery = true)
    void deleteAllByUsername(String username);
    
    @Modifying
    @Query(value = "DELETE FROM expense WHERE category_id IN (SELECT category.id FROM category WHERE category.name= ?1)"
            + "AND user_id IN (SELECT app_user.id FROM app_user WHERE app_user.username= ?2)", nativeQuery = true)
    void deleteAllByCategoryNameAndUsername(String categoryName, String username);

    @Query(value = "SELECT exp.* " +
            "FROM expense exp " +
            "WHERE " +
            "exp.frequency IS NOT NULL " +
            "AND (" +
                "(exp.frequency = '*' AND exp.start_day = ?1) " +
                "OR " +
                "(exp.frequency != '*' AND (@(?2 - exp.start_month))%cast(exp.frequency as int) = 0 AND exp.start_day = ?1) " +
                "OR " +
                "(exp.frequency != '*' AND ?2 = exp.start_month AND exp.start_day = ?1)" +
            ")", nativeQuery = true)
    List<Expense> findRecurrentExpensesToAdd(int currentDay, int currentMonth);
}
