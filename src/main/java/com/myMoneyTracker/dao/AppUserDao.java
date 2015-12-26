package com.myMoneyTracker.dao;

import java.util.List;

import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.user.AppUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by tudor.grigoriu on 17.12.2015.
 * Data access object class for app_user
 */
@Transactional
@Repository
public interface AppUserDao extends JpaRepository<AppUser, Long>{

	@Query("SELECT u FROM AppUser u WHERE u.username = ?1")
	AppUser findUserByUsername(String username);
	
}
