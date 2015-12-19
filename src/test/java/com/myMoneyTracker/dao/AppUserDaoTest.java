package com.myMoneyTracker.dao;

import com.myMoneyTracker.model.user.AppUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
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
    private String FIRST_NAME = "Tudor";
    private static final Logger logger = Logger.getLogger(AppUserDaoTest.class.getName());

    @Test
    public void shouldSaveAppUser(){
        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.save(appUser);
        logger.info("The user has id = " + appUser.getId());
        assertTrue(appUser.getId() != 0);
    }

    private AppUser createAppUser(String firstName) {
        AppUser appUser = new AppUser();
        appUser.setFirstName(firstName);
        appUser.setSurname("Grigoriu");
        appUser.setBirthdate(new Date());
        return appUser;
    }

    @Test
    public void shouldFindAppUser(){
        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.save(appUser);
        appUser = appUserDao.findOne(appUser.getId());
        assertTrue(appUser != null);
    }

    @Test
    public void shouldNotFindAppUser(){
        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.save(appUser);
        appUser = appUserDao.findOne(new Random().nextLong());
        assertTrue(appUser == null);
    }

    @Test
    public void shouldDeleteAppUser(){
        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.save(appUser);
        appUserDao.delete(appUser);
        appUser = appUserDao.findOne(appUser.getId());
        assertTrue(appUser == null);
    }

    @Test
    public void shouldUpdateAppUser(){
        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.save(appUser);
        appUser.setSurname("Florin");
        AppUser result = appUserDao.save(appUser);
        assertTrue(result.getSurname().equals("Florin"));
    }

    @Test
    public void shouldSaveAndFlush(){
        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.saveAndFlush(appUser);
        assertTrue(appUser.getId() > 0);
    }

    @Test
    public void shouldFindAll(){
        AppUser appUser = createAppUser(FIRST_NAME);
        AppUser appUser2 = createAppUser("Florin");
        appUserDao.save(appUser);
        appUserDao.save(appUser2);
        List<AppUser> appUserList = appUserDao.findAll();
        assertEquals(appUserList.size(),2);
    }

}
