package com.myMoneyTracker.dao;

import com.myMoneyTracker.model.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by tudor.grigoriu on 17.12.2015.
 * Data access object class for app_user
 */
@Transactional
@Repository
public interface AppUserDao extends JpaRepository<AppUser, Long>{

}
