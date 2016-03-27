package com.myMoneyTracker.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.myMoneyTracker.model.user.AppUser;

/**
 * @author Tudor Grigoriu
 * This class represents the test class for the app user data access object
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
public class AppUserDaoTest {

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private IncomeDao incomeDao;
    
    @Autowired
    private UserRegistrationDao userRegistrationDao;

    private String FIRST_NAME = "Tudor";
    private static final Logger logger = Logger.getLogger(AppUserDaoTest.class.getName());

    @Before
    public void deleteData() {

        userRegistrationDao.deleteAll();
        userRegistrationDao.flush();
        incomeDao.deleteAll();
        incomeDao.flush();
        appUserDao.deleteAll();
        appUserDao.flush();
    }

    @Test
    public void shouldSaveAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.save(appUser);
        logger.info("The user has id = " + appUser.getId());
        assertTrue(appUser.getId() != 0);
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    public void shouldNotSaveWithTheSameEmail() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUserDao.saveAndFlush(appUser);

        AppUser appUser1 = createAppUser(FIRST_NAME);
        appUserDao.saveAndFlush(appUser1);
    }

    @Test
    public void shouldFindAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.save(appUser);
        appUser = appUserDao.findOne(appUser.getId());
        assertTrue(appUser != null);
    }

    @Test
    public void shouldNotFindAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.save(appUser);
        appUser = appUserDao.findOne(new Random().nextLong());
        assertTrue(appUser == null);
    }

    @Test
    public void shouldDeleteAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.save(appUser);
        appUserDao.delete(appUser);
        appUser = appUserDao.findOne(appUser.getId());
        assertTrue(appUser == null);
    }

    @Test
    public void shouldUpdateAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.save(appUser);
        appUser.setSurname("Florin");
        AppUser result = appUserDao.save(appUser);
        assertTrue(result.getSurname().equals("Florin"));
    }

    @Test
    public void shouldSaveAndFlush() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser = appUserDao.saveAndFlush(appUser);
        assertTrue(appUser.getId() > 0);
    }

    @Test
    public void shouldFindAll() {

        AppUser appUser = createAppUser(FIRST_NAME);
        AppUser appUser2 = createAppUser("Florin");
        appUser2.setUsername("florin");
        appUser2.setEmail("test@test.com");
        appUserDao.save(appUser);
        appUserDao.save(appUser2);
        List<AppUser> appUserList = appUserDao.findAll();
        assertEquals(appUserList.size(), 2);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldFailEmailValidation() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser.setEmail("wrongEmailFormat");
        appUser = appUserDao.saveAndFlush(appUser);
    }

    private AppUser createAppUser(String firstName) {

        AppUser appUser = new AppUser();
        appUser.setFirstName(firstName);
        appUser.setSurname("Grigoriu");
        appUser.setPassword("TEST_PASS");
        appUser.setUsername("tudorgrig");
        appUser.setBirthdate(new Date());
        appUser.setEmail("my-money-tracker@gmail.com");
        return appUser;
    }
}
