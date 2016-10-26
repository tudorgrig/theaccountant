package com.TheAccountant.dao;

import com.TheAccountant.model.expense.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * Data access object class for 'expense'
 *
 * @author Florin, on 20.12.2015
 */
@Transactional
public interface ExpenseDao extends JpaRepository<Expense, Long> {
    
    @Query("SELECT DISTINCT e FROM Expense e WHERE e.user.username = ?1")
    Set<Expense> findByUsername(String username);

    @Modifying
    @Query(value = "DELETE FROM expense WHERE userid IN (SELECT app_user.userId FROM app_user WHERE app_user.username= ?1)", nativeQuery = true)
    void deleteAllByUsername(String username);

    @Modifying
    @Query(value = "DELETE FROM expense WHERE category_id IN (SELECT category.id FROM category WHERE category.id= ?1)"
            + "AND userid IN (SELECT app_user.userId FROM app_user WHERE app_user.username= ?2)", nativeQuery = true)
    void deleteAllByCategoryAndUsername(long categoryId, String username);

    @Query(value = "SELECT exp.* " +
            "FROM expense exp " +
            "WHERE " +
            "exp.frequency != 0 " +
            "AND " +
                "( (@(?2 - cast(Extract(month from exp.creationDate) as int))%exp.frequency = 0 AND cast(Extract(day from exp.creationDate) as int) = ?1 ) " +
                "OR " +
                "(?2 = cast(Extract(month from exp.creationDate) as int) AND cast(Extract(day from exp.creationDate) as int) = ?1) " +
            ")", nativeQuery = true)
    List<Expense> findRecurrentExpensesToAdd(int currentDay, int currentMonth);

    @Query("SELECT DISTINCT e FROM Expense e " +
            "WHERE e.user.username = ?1" +
            " AND e.creationDate BETWEEN ?2 AND ?3")
    Set<Expense> findByTimeInterval(String username, Timestamp startDate, Timestamp endDate);

    @Query("SELECT DISTINCT e FROM Expense e " +
            "WHERE e.user.username = ?1 " +
            " AND e.category.id = ?2" +
            " AND e.creationDate BETWEEN ?3 AND ?4")
    Set<Expense> findByTimeIntervalAndCategory(String username, long categoryId,
                                               Timestamp startDate, Timestamp endDate);
}
