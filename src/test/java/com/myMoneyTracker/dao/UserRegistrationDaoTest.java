package com.myMoneyTracker.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.myMoneyTracker.controller.AppUserController;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.model.user.UserRegistration;

/**
 * 
 * Test class for user refostration dao.
 * 
 * @author Florin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
public class UserRegistrationDaoTest {

    @Autowired
    private UserRegistrationDao userRegistrationDao;
    
    @Autowired
    private AppUserDao appUserDao;
    
    @Autowired
    private AppUserController appUserController;
    
    @Test
    public void shouldFindAndDeleteByUser() {

        AppUser appUser = createAppUser("Florin");
        appUserController.createAppUser(appUser);
        List<UserRegistration> regList = userRegistrationDao.findByUserId(appUser.getId());
        assertFalse("Could not find userRegistration!", regList.isEmpty());
        UserRegistration userRegistration = regList.get(0);
        userRegistrationDao.delete(userRegistration);
        regList = userRegistrationDao.findByUserId(appUser.getId());
        assertTrue("userRegistration should be deleted!", regList.isEmpty());
    }
    
    private AppUser createAppUser(String firstName) {

        AppUser appUser = new AppUser();
        appUser.setFirstName(firstName);
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setUsername("florin");
        appUser.setBirthdate(new Date());
        appUser.setEmail("rampageflo@gmail.com");
        return appUser;
    }
}
