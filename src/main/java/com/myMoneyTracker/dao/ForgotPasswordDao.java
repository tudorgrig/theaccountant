package com.myMoneyTracker.dao;

import com.myMoneyTracker.model.user.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Florin Iacob.
 * Data access object class for {@link com.myMoneyTracker.model.user.ForgotPassword}
 */
@Transactional
@Repository
public interface ForgotPasswordDao extends JpaRepository<ForgotPassword, Long>  {

    ForgotPassword findByCode(String code);
 
    @Modifying
    @Query(value = "delete from forgot_password WHERE user_id = ?1", nativeQuery = true)
    void deleteByUserId(long id);
    
    @Query(value = "select * from forgot_password WHERE user_id = ?1", nativeQuery = true)
    List<ForgotPassword> findByUserId(long id);
    
}
