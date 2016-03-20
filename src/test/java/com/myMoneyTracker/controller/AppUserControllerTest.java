package com.myMoneyTracker.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;

import com.myMoneyTracker.controller.exception.BadRequestException;
import com.myMoneyTracker.controller.exception.ConflictException;
import com.myMoneyTracker.controller.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.myMoneyTracker.dao.AppUserDao;
import com.myMoneyTracker.dao.IncomeDao;
import com.myMoneyTracker.dao.UserRegistrationDao;
import com.myMoneyTracker.dto.user.AppUserDTO;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.model.user.UserRegistration;
import org.springframework.web.client.ResourceAccessException;

/**
 * @author Floryn
 *         Test class for the AppUserController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-config.xml"})
public class AppUserControllerTest {

    @Autowired
    AppUserController appUserController;
    private String FIRST_NAME = "Tudor";

    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private AppUserDao appUserDao;

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
        ResponseEntity<?> responseEntity = appUserController.createAppUser(appUser);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        userRegistrationDao.deleteByUserId(((AppUserDTO) responseEntity.getBody()).getId());
        assertTrue(((AppUserDTO) responseEntity.getBody()).getId() > 0);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldNotCreateAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser.setEmail("wrongFormat");
        appUserController.createAppUser(appUser);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotCreateAppUserWithInvalidMail() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser.setEmail("invalid_user@invalid_host.com");
        ResponseEntity<?> responseEntity = appUserController.createAppUser(appUser);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test(expected = ConflictException.class)
    public void shouldNotCreateDuplicateAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity<?> responseEntity = appUserController.createAppUser(appUser);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((AppUserDTO) responseEntity.getBody()).getId() > 0);
        appUser = createAppUser(FIRST_NAME);
        appUserController.createAppUser(appUser);
    }

    @Test
    public void shouldFindAllUsers() {

        for (int i = 0; i < 5; i++) {
            AppUser appUser = createAppUser(FIRST_NAME);
            appUser.setEmail("email" + i + "@gmail.com");
            appUser.setUsername("tudorgrig" + i);
            ResponseEntity<?> responseEntity = appUserController.createAppUser(appUser);
            assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
            assertTrue(((AppUserDTO) responseEntity.getBody()).getId() > 0);
        }
        ResponseEntity<?> responseEntity = appUserController.listAllUsers();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(5, ((List<?>) responseEntity.getBody()).size());
    }

    @Test
    public void shouldFindEmptyListOfUsers() {

        ResponseEntity<?> responseEntity = appUserController.listAllUsers();
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(null, responseEntity.getBody());
    }

    @Test
    public void shouldFindOneUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity<?> responseEntity = appUserController.createAppUser(appUser);
        long id = ((AppUserDTO) responseEntity.getBody()).getId();
        ResponseEntity<?> found = appUserController.findAppUser(id);
        assertEquals(HttpStatus.OK, found.getStatusCode());
        assertTrue(found.getBody() != null);
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotFindOneUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity<?> responseEntity = appUserController.createAppUser(appUser);
        long id = ((AppUserDTO) responseEntity.getBody()).getId();
        appUserController.findAppUser(id + 1);
    }

    @Test
    public void shouldUpdateUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity<?> responseEntity = appUserController.createAppUser(appUser);
        long id = ((AppUserDTO) responseEntity.getBody()).getId();
        AppUser toUpdateAppUser = createAppUser("Florin");
        ResponseEntity<?> updated = appUserController.updateAppUser(id, toUpdateAppUser);
        assertEquals(HttpStatus.NO_CONTENT, updated.getStatusCode());
        assertEquals("User updated", updated.getBody());
        ResponseEntity<?> updatedUser = appUserController.findAppUser(id);
        assertEquals("Florin", ((AppUserDTO) updatedUser.getBody()).getFirstName());
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotUpdateUser() {

        AppUser toUpdateAppUser = createAppUser("Florin");
        appUserController.updateAppUser(-1l, toUpdateAppUser);
    }

    @Test
    public void shouldDeleteAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity<?> responseEntity = appUserController.createAppUser(appUser);
        userRegistrationDao.deleteAll();
        ResponseEntity<?> deletedEntity = appUserController.deleteAppUser(((AppUserDTO) responseEntity.getBody()).getId());
        assertEquals(HttpStatus.NO_CONTENT, deletedEntity.getStatusCode());
        assertEquals("User deleted", deletedEntity.getBody());
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotDeleteAppUser() {

        ResponseEntity<?> deletedEntity = appUserController.deleteAppUser(1l);
        assertEquals(HttpStatus.NOT_FOUND, deletedEntity.getStatusCode());
    }

    @Test
    public void shouldRegisterAndActivateUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUserController.createAppUser(appUser);
        assertFalse("User should NOT be activated!", appUser.isActivated());
        List<UserRegistration> regList = userRegistrationDao.findByUserId(appUser.getId());
        assertFalse("Could not find userRegistration!", regList.isEmpty());
        appUserController.registerUser(regList.get(0).getCode());
        appUser = appUserDao.findOne(appUser.getId());
        assertTrue("User should be activated!", appUser.isActivated());
    }

    @Test
    public void shouldLoginWithUsername() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String password = appUser.getPassword();
        appUserController.createAppUser(appUser);

        List<UserRegistration> regList = userRegistrationDao.findByUserId(appUser.getId());
        assertFalse("Could not find userRegistration!", regList.isEmpty());
        appUserController.registerUser(regList.get(0).getCode());

        AppUser toLoginAppUser = new AppUser();
        toLoginAppUser.setPassword(password);
        toLoginAppUser.setUsername("tudorgrig");
        ResponseEntity<?> loginResponseEntity = appUserController.login(toLoginAppUser);
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotLoginNonActivatedUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String password = appUser.getPassword();
        appUserController.createAppUser(appUser);

        AppUser toLoginAppUser = new AppUser();
        toLoginAppUser.setPassword(password);
        toLoginAppUser.setUsername("tudorgrig");
        appUserController.login(toLoginAppUser);

    }

    @Test
    public void shouldLoginWithEmail() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String password = appUser.getPassword();
        appUserController.createAppUser(appUser);

        List<UserRegistration> regList = userRegistrationDao.findByUserId(appUser.getId());
        assertFalse("Could not find userRegistration!", regList.isEmpty());
        appUserController.registerUser(regList.get(0).getCode());

        AppUser toLoginAppUser = new AppUser();
        toLoginAppUser.setPassword(password);
        toLoginAppUser.setUsername("my-money-tracker@gmail.com");
        ResponseEntity<?> loginResponseEntity = appUserController.login(toLoginAppUser);
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotLoginWrongUsername() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String password = appUser.getPassword();
        appUserController.createAppUser(appUser);

        List<UserRegistration> regList = userRegistrationDao.findByUserId(appUser.getId());
        assertFalse("Could not find userRegistration!", regList.isEmpty());
        appUserController.registerUser(regList.get(0).getCode());

        AppUser toLoginAppUser = new AppUser();
        toLoginAppUser.setPassword(password);
        toLoginAppUser.setUsername("failure");
        appUserController.login(toLoginAppUser);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotRegisterAndActivateUser() {

        ResponseEntity<?> responseEntity = appUserController.registerUser("invalid_code");
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotLoginIncorrectPassword() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUserController.createAppUser(appUser);

        List<UserRegistration> regList = userRegistrationDao.findByUserId(appUser.getId());
        assertFalse("Could not find userRegistration!", regList.isEmpty());
        appUserController.registerUser(regList.get(0).getCode());

        AppUser toLoginAppUser = new AppUser();
        toLoginAppUser.setPassword("incorrect_pass");
        toLoginAppUser.setUsername("tudorgrig");
        ResponseEntity<?> loginResponseEntity = appUserController.login(toLoginAppUser);
        assertEquals(HttpStatus.BAD_REQUEST, loginResponseEntity.getStatusCode());
        assertEquals("Incorrect password", loginResponseEntity.getBody());
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotLoginNullPassword() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUserController.createAppUser(appUser);

        List<UserRegistration> regList = userRegistrationDao.findByUserId(appUser.getId());
        assertFalse("Could not find userRegistration!", regList.isEmpty());
        appUserController.registerUser(regList.get(0).getCode());

        AppUser toLoginAppUser = new AppUser();
        toLoginAppUser.setUsername("tudorgrig");
        appUserController.login(toLoginAppUser);
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
