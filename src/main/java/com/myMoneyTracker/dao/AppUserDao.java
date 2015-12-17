package com.myMoneyTracker.dao;
import com.myMoneyTracker.model.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

/**
 * Created by tudor.grigoriu on 17.12.2015.
 * Data access object class for app_user
 */
@Transactional
public interface AppUserDao extends JpaRepository<AppUser, Long>{


}
