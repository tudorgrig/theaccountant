package com.myMoneyTracker.dao;

import com.myMoneyTracker.model.user.AppUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * @author Tudor Grigoriu
 * This class represents the test class for the app user data access object
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/spring-config.xml"})
@Transactional
public class AppUserDaoTest {

    @Autowired
    private AppUserDao appUserDao;

    private static final Logger logger = Logger.getLogger(AppUserDaoTest.class.getName());

    @Test
    public void shouldSaveAppUser(){
        AppUser appUser = createAppUser();
        appUser = appUserDao.save(appUser);
        logger.info("The user has id = " + appUser.getId());
        assertTrue(appUser.getId() != 0);
    }


    @Test
    public void shouldFindAppUser(){
        AppUser appUser = createAppUser();
        appUser = appUserDao.save(appUser);
        appUser = appUserDao.findOne(appUser.getId());
        assertTrue(appUser != null);
    }

    @Test
    public void shouldNotFindAppUser(){
        AppUser appUser = createAppUser();
        appUser = appUserDao.save(appUser);
        appUser = appUserDao.findOne(new Random().nextLong());
        assertTrue(appUser == null);
    }

    @Test
    public void shouldDeleteAppUser(){
        AppUser appUser = createAppUser();
        appUser = appUserDao.save(appUser);
        appUserDao.delete(appUser);
        appUser = appUserDao.findOne(appUser.getId());
        assertTrue(appUser == null);
    }

    @Test
    public void shouldUpdateAppUser(){
        AppUser appUser = createAppUser();
        appUser = appUserDao.save(appUser);
        appUser.setSurname("Florin");
        AppUser result = appUserDao.save(appUser);
        assertTrue(result.getSurname().equals("Florin"));
    }

    @Test
    public void shouldSaveAndFlush(){
        AppUser appUser = createAppUser();
        appUser = appUserDao.saveAndFlush(appUser);
        assertTrue(appUser.getId() > 0);
    }

    private AppUser createAppUser() {
    	AppUser appUser = new AppUser();
    	appUser.setFirstName("Tudor");
    	appUser.setSurname("Grigoriu");
    	appUser.setBirthdate(new Date());
    	return appUser;
    }
}
