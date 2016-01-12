package com.myMoneyTracker.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.myMoneyTracker.dao.IncomeDao;
import com.myMoneyTracker.dao.UserRegistrationDao;
import com.myMoneyTracker.model.user.AppUser;

/**
 * @author Tudor Grigoriu
 * Test class for the AppUserController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
public class AppUserControllerTest {

    @Autowired
    AppUserController appUserController;
    private String FIRST_NAME = "Tudor";

    @Autowired
    private IncomeDao incomeDao;
    
    @Autowired
    private UserRegistrationDao userRegistrationDao;

    @Before
    public void deleteAllUsers() {

        userRegistrationDao.deleteAll();
        userRegistrationDao.flush();
        incomeDao.deleteAll();
        incomeDao.flush();
        appUserController.deleteAll();
    }

    @Test
    public void shouldCreateAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity responseEntity = appUserController.createAppUser(appUser);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        userRegistrationDao.deleteByUserId(((AppUser) responseEntity.getBody()).getId());
        assertTrue(((AppUser) responseEntity.getBody()).getId() > 0);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldNotCreateAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser.setEmail("wrongFormat");
        appUserController.createAppUser(appUser);
    }

    @Test
    public void shouldNotCreateDuplicateAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity responseEntity = appUserController.createAppUser(appUser);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((AppUser) responseEntity.getBody()).getId() > 0);
        appUser = createAppUser(FIRST_NAME);
        responseEntity = appUserController.createAppUser(appUser);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    }

    @Test
    public void shouldFindAllUsers() {

        for (int i = 0; i < 5; i++) {
            AppUser appUser = createAppUser(FIRST_NAME);
            appUser.setEmail("email" + i + "@gmail.com");
            appUser.setUsername("tudorgrig" + i);
            ResponseEntity responseEntity = appUserController.createAppUser(appUser);
            assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
            assertTrue(((AppUser) responseEntity.getBody()).getId() > 0);
        }
        ResponseEntity responseEntity = appUserController.listAllUsers();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(5, ((List) responseEntity.getBody()).size());
    }

    @Test
    public void shouldFindEmptyListOfUsers() {

        ResponseEntity responseEntity = appUserController.listAllUsers();
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(null, responseEntity.getBody());
    }

    @Test
    public void shouldFindOneUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity responseEntity = appUserController.createAppUser(appUser);
        long id = ((AppUser) responseEntity.getBody()).getId();
        ResponseEntity<?> found = appUserController.findAppUser(id);
        assertEquals(HttpStatus.OK, found.getStatusCode());
        assertTrue(found.getBody() != null);
    }

    @Test
    public void shouldNotFindOneUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity responseEntity = appUserController.createAppUser(appUser);
        long id = ((AppUser) responseEntity.getBody()).getId();
        ResponseEntity<?> found = appUserController.findAppUser(id + 1);
        assertEquals(HttpStatus.NOT_FOUND, found.getStatusCode());
        assertTrue(found.getBody().equals("User not found"));
    }

    @Test
    public void shouldUpdateUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity responseEntity = appUserController.createAppUser(appUser);
        long id = ((AppUser) responseEntity.getBody()).getId();
        AppUser toUpdateAppUser = createAppUser("Florin");
        ResponseEntity updated = appUserController.updateAppUser(id, toUpdateAppUser);
        assertEquals(HttpStatus.NO_CONTENT, updated.getStatusCode());
        assertEquals("User updated", updated.getBody());
        ResponseEntity updatedUser = appUserController.findAppUser(id);
        assertEquals("Florin", ((AppUser) updatedUser.getBody()).getFirstName());
    }

    @Test
    public void shouldNotUpdateUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity responseEntity = appUserController.createAppUser(appUser);
        long id = ((AppUser) responseEntity.getBody()).getId();
        AppUser toUpdateAppUser = createAppUser("Florin");
        ResponseEntity updated = appUserController.updateAppUser(id + 1, toUpdateAppUser);
        assertEquals(HttpStatus.NOT_FOUND, updated.getStatusCode());
        assertEquals("User not found", updated.getBody());
    }

    @Test
    public void shouldDeleteAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity responseEntity = appUserController.createAppUser(appUser);
        userRegistrationDao.deleteAll();
        ResponseEntity deletedEntity = appUserController.deleteAppUser(((AppUser) responseEntity.getBody()).getId());
        assertEquals(HttpStatus.NO_CONTENT, deletedEntity.getStatusCode());
        assertEquals("User deleted", deletedEntity.getBody());
    }

    @Test
    public void shouldNotDeleteAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity responseEntity = appUserController.createAppUser(appUser);
        ResponseEntity deletedEntity = appUserController.deleteAppUser(((AppUser) responseEntity.getBody()).getId() + 1);
        assertEquals(HttpStatus.NOT_FOUND, deletedEntity.getStatusCode());
    }

    @Test
    public void shouldLoginWithUsername() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUserController.createAppUser(appUser);
        ResponseEntity loginResponseEntity = appUserController.login("tudorgrig");
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());
    }

    @Test
    public void shouldLoginWithEmail() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUserController.createAppUser(appUser);
        ResponseEntity loginResponseEntity = appUserController.login("my-money-tracker@gmail.com");
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());
    }

    @Test
    public void shouldNotLogin() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUserController.createAppUser(appUser);
        ResponseEntity loginResponseEntity = appUserController.login("failure");
        assertEquals(HttpStatus.NOT_FOUND, loginResponseEntity.getStatusCode());
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
