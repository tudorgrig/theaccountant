package com.TheAccountant.controller;

import java.util.Currency;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;

import com.TheAccountant.dao.*;
import com.TheAccountant.dto.currency.DefaultCurrencyDTO;
import com.TheAccountant.dto.user.ChangePasswordDTO;
import com.TheAccountant.dto.user.ForgotPasswordDTO;
import com.TheAccountant.dto.user.RenewForgotPasswordDTO;
import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.user.ForgotPassword;
import com.TheAccountant.util.PasswordEncrypt;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.TheAccountant.app.authentication.SessionAuthentication;
import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.controller.exception.ConflictException;
import com.TheAccountant.controller.exception.NotFoundException;
import com.TheAccountant.dto.user.AppUserDTO;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.model.user.UserRegistration;
import com.TheAccountant.service.SessionService;

import static org.junit.Assert.*;

/**
 * @author Floryn
 *         Test class for the {@link AppUserController}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
public class AppUserControllerTest {

    private String FIRST_NAME = "Floryn";
    private String username = "florin1234";

    @Autowired
    private AppUserController appUserController;

    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private UserRegistrationDao userRegistrationDao;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private ForgotPasswordDao forgotPasswordDao;

    @Autowired
    private AuthenticatedSessionDao authenticatedSessionDao;

    @Autowired
    private PasswordEncrypt passwordEncrypt;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CategoryDao categoryDao;

    @Before
    public void deleteAllUsers() {
        SecurityContextHolder.getContext().setAuthentication(new SessionAuthentication(username, "1.1.1.1"));
    }

    @After
    public void afterSetup(){
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    @Ignore
    public void shouldCreateAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity<?> responseEntity = appUserController.createAppUser(appUser);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((AppUserDTO) responseEntity.getBody()).getUserId() > 0);
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test
    @Ignore
    public void shouldCreateDefaultCategoriesOnRegister() {

        AppUser appUser = createAppUser(FIRST_NAME);
        ResponseEntity<?> responseEntity = appUserController.createAppUser(appUser);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((AppUserDTO) responseEntity.getBody()).getUserId() > 0);

        List<Category> defaultCategories = categoryDao.findByUsername (appUser.getUsername());
        assertNotNull(defaultCategories);
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldNotCreateAppUserInvalidEmail() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser.setEmail("wrongFormat");
        appUserController.createAppUser(appUser);
    }

    @Test(expected = BadRequestException.class)
    @Ignore
    public void shouldNotCreateAppUserWithInvalidMail() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUser.setEmail("invalid_user@invalid_host.com");
        appUserController.createAppUser(appUser);
    }

    @Test
    public void shouldNotCreateDuplicateAppUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUserDao.saveAndFlush(appUser);
        long id = appUser.getUserId();
        appUser = createAppUser(FIRST_NAME);
        try {
            appUserController.createAppUser(appUser);
        } catch(Exception e){
            assertTrue(e instanceof ConflictException);
            appUserDao.delete(id);
            appUserDao.flush();
        }


    }

    @Test
    public void shouldFindOneUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUserDao.save(appUser);
        ResponseEntity<?> found = appUserController.findAppUser(appUser.getUserId());
        assertEquals(HttpStatus.OK, found.getStatusCode());
        assertTrue(found.getBody() != null);
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotFindOneUser() {
        appUserController.findAppUser(-1L);
    }

    @Test
    public void shouldUpdateUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUserDao.save(appUser);
        AppUser toUpdateAppUser = createAppUser("Florin");
        ResponseEntity<?> updated = appUserController.updateAppUser(appUser.getUserId(), toUpdateAppUser);
        assertEquals(HttpStatus.NO_CONTENT, updated.getStatusCode());
        assertEquals("User updated", updated.getBody());
        ResponseEntity<?> updatedUser = appUserController.findAppUser(appUser.getUserId());
        assertEquals("Florin", ((AppUserDTO) updatedUser.getBody()).getFirstName());
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotUpdateUser() {

        AppUser toUpdateAppUser = createAppUser("Florin");
        appUserController.updateAppUser(-1l, toUpdateAppUser);
    }

    @Test
    public void shouldRegisterAndActivateUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        appUserController.createAppUser(appUser);
        assertFalse("User should NOT be activated!", appUser.isActivated());
        List<UserRegistration> regList = userRegistrationDao.findByUserId(appUser.getUserId());
        assertFalse("Could not find userRegistration!", regList.isEmpty());
        appUserController.registerUser(regList.get(0).getCode());
        appUser = appUserDao.findOne(appUser.getUserId());
        assertTrue("User should be activated!", appUser.isActivated());
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test
    public void shouldLoginWithUsername() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String uncryptedPassword = appUser.getPassword();
        String cryptedPassword = passwordEncrypt.encryptPassword(appUser.getPassword());
        appUser.setPassword(cryptedPassword);
        appUser.setActivated(true);
        String username = appUser.getUsername();
        appUserDao.save(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(username, uncryptedPassword);
        ResponseEntity<?> loginResponseEntity = appUserController.login(authorizationString);
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test
    public void shouldSetAndGetDefaultCurrency() {

        String USD_CURRENCY = "USD";

        AppUser appUser = createAppUser(FIRST_NAME);
        String uncryptedPassword = appUser.getPassword();
        String cryptedPassword = passwordEncrypt.encryptPassword(appUser.getPassword());
        appUser.setPassword(cryptedPassword);
        appUser.setActivated(true);
        String username = appUser.getUsername();
        appUserDao.save(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(username, uncryptedPassword);
        ResponseEntity<?> loginResponseEntity = appUserController.login(authorizationString);
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());

        appUserController.setDefaultCurrency(new DefaultCurrencyDTO(USD_CURRENCY));

        ResponseEntity<?> response = appUserController.getDefaultCurrency();
        DefaultCurrencyDTO responseDTO = (DefaultCurrencyDTO) response.getBody();
        assertTrue("The Value Of the returned DefaultCurrency is invalid!", responseDTO.getValue().equals(USD_CURRENCY));
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();

    }

    @Test
    public void shouldNotSetDefaultCurrency() {

        String INVALID_CURRENCY = "INVALID";

        AppUser appUser = createAppUser(FIRST_NAME);
        String uncryptedPassword = appUser.getPassword();
        String cryptedPassword = passwordEncrypt.encryptPassword(appUser.getPassword());
        appUser.setPassword(cryptedPassword);
        appUser.setActivated(true);
        String username = appUser.getUsername();
        appUserDao.save(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(username, uncryptedPassword);
        ResponseEntity<?> loginResponseEntity = appUserController.login(authorizationString);
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());

        boolean exceptionThrown = false;
        try {
            appUserController.setDefaultCurrency(new DefaultCurrencyDTO(INVALID_CURRENCY));
        } catch (Exception e) {
            exceptionThrown = true;
            assertTrue("Setting invalid Currency should throw BadRequestException!", e instanceof BadRequestException);
        }

        assertTrue("Setting invalid Currency should throw Exception!", exceptionThrown == true);
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test
    public void shouldNotLoginNonActivatedUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String username = appUser.getUsername();
        String password = appUser.getPassword();
        appUserDao.save(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(username, password);
        try {
            appUserController.login(authorizationString);
        }catch(Exception e){
            assertTrue(e instanceof BadRequestException);
            appUserDao.delete(appUser.getUserId());
            appUserDao.flush();
        }

    }

    @Test
    public void shouldLoginWithEmail() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String email = appUser.getEmail();
        String uncryptedPassword = appUser.getPassword();
        String password = passwordEncrypt.encryptPassword(appUser.getPassword());;
        appUser.setPassword(password);
        appUser.setActivated(true);
        appUserDao.save(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(email, uncryptedPassword);
        ResponseEntity<?> loginResponseEntity = appUserController.login(authorizationString);
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test
    public void shouldNotLoginWrongUsername() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String password = appUser.getPassword();
        appUserDao.saveAndFlush(appUser);
        String authorizationString = sessionService.encodeUsernameAndPassword("WrongUsername", password);
        try{
            appUserController.login(authorizationString);
        } catch(Exception e){
            assertTrue(e instanceof NotFoundException);
            appUserDao.delete(appUser.getUserId());
            appUserDao.flush();
        }

    }

    @Test
    public void shouldNotLoginIncorrectPassword() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String username = appUser.getUsername();
        appUser.setActivated(true);
        appUserDao.save(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(username, "wrong_pass");
        try{
            appUserController.login(authorizationString);
        }catch(Exception e){
            assertTrue(e instanceof BadRequestException);
            appUserDao.delete(appUser.getUserId());
            appUserDao.flush();
        }

    }

    @Test
    public void shouldNotLoginNullPassword() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String username = appUser.getUsername();
        appUser.setActivated(true);
        appUserDao.save(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(username, null);
        try{
            appUserController.login(authorizationString);
        } catch(Exception e){
            assertTrue(e instanceof BadRequestException);
            appUserDao.delete(appUser.getUserId());
            appUserDao.flush();
        }

    }

    @Test
    public void shouldChangePassword() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String email = appUser.getEmail();
        String uncryptedPassword = appUser.getPassword();
        String password = passwordEncrypt.encryptPassword(appUser.getPassword());;
        appUser.setPassword(password);
        appUser.setActivated(true);
        appUser = appUserDao.saveAndFlush(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(email, uncryptedPassword);
        ResponseEntity<?> loginResponseEntity = appUserController.login(authorizationString);
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());

        String newPassword = "Florin1234";
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setNp(newPassword);
        changePasswordDTO.setOp(uncryptedPassword);
        appUserController.changePassword(changePasswordDTO);

        appUser = appUserDao.findOne(appUser.getUserId());
        assertEquals("The password for the user should be changed!",
                passwordEncrypt.encryptPassword(newPassword),
                appUser.getPassword());

        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test
    public void shouldNotChangeTooShortPassword() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String email = appUser.getEmail();
        String uncryptedPassword = appUser.getPassword();
        String password = passwordEncrypt.encryptPassword(appUser.getPassword());;
        appUser.setPassword(password);
        appUser.setActivated(true);
        appUser = appUserDao.saveAndFlush(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(email, uncryptedPassword);
        ResponseEntity<?> loginResponseEntity = appUserController.login(authorizationString);
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());

        String newPassword = "1234";
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setNp(newPassword);
        changePasswordDTO.setOp(uncryptedPassword);
        boolean exceptionThrown = false;
        try {
            appUserController.changePassword(changePasswordDTO);
        } catch (BadRequestException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().equals("Password must have at least 8 characters!"));
        }

        assertTrue("Should NOT change password having less than 8 characters!", exceptionThrown);
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test
    public void shouldNotChangePasswordInvalidOldPassword() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String email = appUser.getEmail();
        String uncryptedPassword = appUser.getPassword();
        String password = passwordEncrypt.encryptPassword(appUser.getPassword());;
        appUser.setPassword(password);
        appUser.setActivated(true);
        appUser = appUserDao.saveAndFlush(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(email, uncryptedPassword);
        ResponseEntity<?> loginResponseEntity = appUserController.login(authorizationString);
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());

        String newPassword = "Florin1234";
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setNp(newPassword);
        changePasswordDTO.setOp("invalidPassword");
        boolean exceptionThrown = false;
        try {
            appUserController.changePassword(changePasswordDTO);
        } catch (BadRequestException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().equals("Invalid parameters!"));
        }

        assertTrue("Should NOT change password having less than 8 characters!", exceptionThrown);
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test
    public void shouldNotChangePasswordForEmptyOldPassword() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String email = appUser.getEmail();
        String uncryptedPassword = appUser.getPassword();
        String password = passwordEncrypt.encryptPassword(appUser.getPassword());;
        appUser.setPassword(password);
        appUser.setActivated(true);
        appUser = appUserDao.saveAndFlush(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(email, uncryptedPassword);
        ResponseEntity<?> loginResponseEntity = appUserController.login(authorizationString);
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());

        String newPassword = "Florin1234";
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setNp(newPassword);
        changePasswordDTO.setOp("");
        boolean exceptionThrown = false;
        try {
            appUserController.changePassword(changePasswordDTO);
        } catch (BadRequestException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().equals("Invalid request!"));
        }
        assertTrue("Should NOT change password having less than 8 characters!", exceptionThrown);
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test
    public void shouldNotSendForgotPasswordEmailForNullEmail() {

        boolean exceptionThrown = false;
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO(null);
        try {
            appUserController.sendForgotPasswordMail(forgotPasswordDTO);
        } catch (BadRequestException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().equals("Invalid request!"));
        }

        assertTrue("Should NOT send forgot password mail for null email!", exceptionThrown);
    }

    @Test
    public void shouldNotSendForgotPasswordEmailForIncorrectEmail() {

        boolean exceptionThrown = false;
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO("incorrect_email@");
        try {
            appUserController.sendForgotPasswordMail(forgotPasswordDTO);
        } catch (BadRequestException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().equals("Invalid email address!"));
        }

        assertTrue("Should NOT send forgot password mail for incorrect email!", exceptionThrown);
    }

    @Test
    public void shouldNotSendForgotPasswordEmailForUnregisteredEmail() {

        boolean exceptionThrown = false;
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO("inexistent_mail@gmail.com");
        try {
            appUserController.sendForgotPasswordMail(forgotPasswordDTO);
        } catch (BadRequestException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().equals("Invalid request attempt!"));
        }

        assertTrue("Should NOT send forgot password mail for unregistered email!", exceptionThrown);
    }

    @Test
    public void shouldSendForgotPasswordEmail() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String cryptedPassword = passwordEncrypt.encryptPassword(appUser.getPassword());
        appUser.setActivated(true);
        appUser.setPassword(cryptedPassword);
        appUserDao.saveAndFlush(appUser);

        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO(appUser.getEmail());
        try {
            appUserController.sendForgotPasswordMail(forgotPasswordDTO);
        } catch (Exception e) {
            appUserDao.delete(appUser.getUserId());
            appUserDao.flush();
            fail(e.getMessage());
        }
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test
    public void shouldNotRenewForgotPasswordForInvalidCodeOrNewPassword() {

        boolean exceptionThrown = false;
        RenewForgotPasswordDTO renewForgotPasswordDTO = new RenewForgotPasswordDTO("", "Pasword1234");
        try {
            appUserController.renewForgotPassword(renewForgotPasswordDTO);
        } catch (BadRequestException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().equals("Invalid request!"));
        }

        assertTrue("Should NOT renew forgot password for invalid request!", exceptionThrown);

        exceptionThrown = false;
        renewForgotPasswordDTO = new RenewForgotPasswordDTO("code123", null);
        try {
            appUserController.renewForgotPassword(renewForgotPasswordDTO);
        } catch (BadRequestException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().equals("Invalid request!"));
        }

        assertTrue("Should NOT renew forgot password for invalid request!", exceptionThrown);
    }

    @Test
    public void shouldNotRenewForgotPasswordForNonExistentCode() {

        boolean exceptionThrown = false;
        RenewForgotPasswordDTO renewForgotPasswordDTO = new RenewForgotPasswordDTO("non-existent-code-1234", "Pasword1234");
        try {
            appUserController.renewForgotPassword(renewForgotPasswordDTO);
        } catch (BadRequestException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().equals("Invalid renew attempt!"));
        }

        assertTrue("Should NOT renew forgot password for non-existent code!", exceptionThrown);
    }

    @Test
    public void shouldRenewForgotPassword() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String cryptedPassword = passwordEncrypt.encryptPassword(appUser.getPassword());
        appUser.setActivated(true);
        appUser.setPassword(cryptedPassword);
        appUserDao.saveAndFlush(appUser);

        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO(appUser.getEmail());
        try {
            appUserController.sendForgotPasswordMail(forgotPasswordDTO);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        ForgotPassword forgotPassword = forgotPasswordDao.findByUserId(appUser.getUserId()).get(0);
        String newPassword = "NewPass1234";
        RenewForgotPasswordDTO renewForgotPasswordDTO = new RenewForgotPasswordDTO(forgotPassword.getCode(), newPassword);
        try {
            appUserController.renewForgotPassword(renewForgotPasswordDTO);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        appUser = appUserDao.findOne(appUser.getUserId());
        assertEquals("The new password was NOT set for the User",
                passwordEncrypt.encryptPassword(newPassword),
                appUser.getPassword());

        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    @Test
    public void shouldLogoutUser() {

        AppUser appUser = createAppUser(FIRST_NAME);
        String uncryptedPassword = appUser.getPassword();
        String cryptedPassword = passwordEncrypt.encryptPassword(appUser.getPassword());
        String username = appUser.getUsername();
        appUser.setActivated(true);
        appUser.setPassword(cryptedPassword);
        appUserDao.save(appUser);

        String authorizationString = sessionService.encodeUsernameAndPassword(username, uncryptedPassword);
        ResponseEntity<?> loginResponseEntity = appUserController.login(authorizationString);
        assertEquals(HttpStatus.OK, loginResponseEntity.getStatusCode());

        ResponseEntity<?> logoutResponse = appUserController.logout(authorizationString);
        assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());
        appUserDao.delete(appUser.getUserId());
        appUserDao.flush();
    }

    public void shouldNotLogoutUser() {

        String authorizationString = sessionService.encodeUsernameAndPassword(username, "invalid_password");
        ResponseEntity<?> logoutResponse = appUserController.logout(authorizationString);
        assertEquals(HttpStatus.BAD_REQUEST, logoutResponse.getStatusCode());
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotRegisterAndActivateUser() {

        ResponseEntity<?> responseEntity = appUserController.registerUser("invalid_code");
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    private AppUser createAppUser(String firstName) {

        AppUser appUser = new AppUser();
        appUser.setFirstName(firstName);
        appUser.setSurname("Grigoriu");
        appUser.setPassword("TEST_PASS");
        appUser.setUsername(username);
        appUser.setBirthdate(new Date());
        appUser.setEmail("my-money-tracker@gmail.com");
        appUser.setDefaultCurrency(Currency.getInstance("RON"));
        return appUser;
    }
}
