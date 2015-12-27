package com.myMoneyTracker.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myMoneyTracker.model.user.AppUser;

/**
 * Created by tudor.grigoriu on 17.12.2015.
 * Data access object class for app_user
 */
@Transactional
@Repository
public interface AppUserDao extends JpaRepository<AppUser, Long>{

    AppUser findByEmail(String loginString);

    AppUser findByUsername(String loginString);

}
