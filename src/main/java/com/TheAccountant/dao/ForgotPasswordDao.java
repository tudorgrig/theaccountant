package com.TheAccountant.dao;

import com.TheAccountant.model.user.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Florin Iacob.
 * Data access object class for {@link com.TheAccountant.model.user.ForgotPassword}
 */
@Transactional
@Repository
public interface ForgotPasswordDao extends JpaRepository<ForgotPassword, Long>  {

    ForgotPassword findByCode(String code);
    
    @Query(value = "select * from forgot_password WHERE user_id = ?1", nativeQuery = true)
    List<ForgotPassword> findByUserId(long id);
    
}
