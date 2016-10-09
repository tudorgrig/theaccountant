package com.TheAccountant.dao;

import com.TheAccountant.model.user.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Florin Iacob.
 * Data access object class for user_registration
 */
@Transactional
@Repository
public interface UserRegistrationDao extends JpaRepository<UserRegistration, Long>  {
    
    UserRegistration findByCode(String code);
 
    @Modifying
    @Query(value = "delete from user_registration WHERE user_id = ?1", nativeQuery = true)
    void deleteByUserId(long id);
    
    @Query(value = "select * from user_registration WHERE user_id = ?1", nativeQuery = true)
    List<UserRegistration> findByUserId(long id);
    
}
