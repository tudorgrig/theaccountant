package com.myMoneyTracker.dao;

import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.expense.RecurrentExpenseEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by tudor.grigoriu on 22.04.2016.
 */
public interface RecurrentExpenseEventDao extends JpaRepository<RecurrentExpenseEvent, Long> {

    @Query(value = "SELECT exp.* " +
            "FROM expense exp " +
            "JOIN recurrent_expense_event ree " +
                    "ON exp.id = ree.expense_id " +
            "WHERE " +
                "(ree.frequency = '*' AND ree.start_day = ?1) " +
                "OR " +
                "(ree.frequency != '*' AND (@(?2 - ree.start_month))%cast(ree.frequency as int) = 0)", nativeQuery = true)
    List<Expense> findRecurrentExpensesToAdd(int startDay, int startMonth);
}
