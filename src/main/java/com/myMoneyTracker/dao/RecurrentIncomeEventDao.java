package com.myMoneyTracker.dao;

import com.myMoneyTracker.model.income.Income;
import com.myMoneyTracker.model.income.RecurrentIncomeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by tudor.grigoriu on 22.04.2016.
 */
public interface RecurrentIncomeEventDao extends JpaRepository<RecurrentIncomeEvent, Long> {

    @Query(value = "SELECT * FROM income as inc " +
            "JOIN recurrent_income_event as rie " +
            "ON inc.id = rie.income_id " +
            "WHERE " +
            "(rie.frequency = '*' AND rie.start_day = ?1) " +
            "OR " +
            "(rie.frequency != '*' AND (@(?2 - rie.start_month))%cast(rie.frequency as int) = 0)", nativeQuery = true)
    List<Income> findRecurrentIncomesToAdd(int startDay, int startMonth);
}
