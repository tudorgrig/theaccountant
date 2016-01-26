package com.myMoneyTracker.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.myMoneyTracker.model.income.Income;

/**
 * Data access object class for 'income'
 *
 * @author Florin, on 19.12.2015
 */
@Transactional
public interface IncomeDao extends JpaRepository<Income, Long> {

    List<Income> findByUserId(Long userId);
    
    @Query("SELECT i FROM Income i WHERE i.user.username = ?1")
    List<Income> findByUsername(String username);
    
    @Modifying
    @Query(value = "DELETE FROM income WHERE user_id IN (SELECT app_user.id FROM app_user WHERE app_user.username= ?1)", nativeQuery = true)
    void deleteAllByUsername(String username);

}
