package com.TheAccountant.controller;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.controller.exception.ConflictException;
import com.TheAccountant.controller.exception.NotFoundException;
import com.TheAccountant.converter.AppUserConverter;
import com.TheAccountant.dao.AppUserDao;
import com.TheAccountant.dao.ForgotPasswordDao;
import com.TheAccountant.dao.UserRegistrationDao;
import com.TheAccountant.dto.currency.DefaultCurrencyDTO;
import com.TheAccountant.dto.user.AppUserDTO;
import com.TheAccountant.dto.user.ChangePasswordDTO;
import com.TheAccountant.dto.user.ForgotPasswordDTO;
import com.TheAccountant.dto.user.RenewForgotPasswordDTO;
import com.TheAccountant.model.session.AuthenticatedSession;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.model.user.ForgotPassword;
import com.TheAccountant.model.user.UserRegistration;
import com.TheAccountant.service.SessionService;
import com.TheAccountant.util.ControllerUtil;
import com.TheAccountant.util.EmailValidator;
import com.TheAccountant.util.PasswordEncrypt;
import com.TheAccountant.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Currency;

/**
 * Rest Controller for AppUser entity.
 *
 * @author Tudor
 */
@RestController
@RequestMapping(value = "/user")
public class AppUserController {
    
    @Autowired
    private AppUserDao appUserDao;
    
    @Autowired
    private UserRegistrationDao userRegistrationDao;

    @Autowired
    private ForgotPasswordDao forgotPasswordDao;
    
    @Autowired
    private EmailValidator emailValidator;
    
    @Autowired
    private PasswordEncrypt passwordEncrypt;
    
    @Autowired
    private UserUtil userUtil;
    
    @Autowired
    private AppUserConverter appUserConverter;
    
    @Autowired
    private SessionService sessionService;
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<AppUserDTO> createAppUser(@RequestBody @Valid AppUser appUser) {
    
        String encryptedPassword = passwordEncrypt.encryptPassword(appUser.getPassword());
        appUser.setPassword(encryptedPassword);
        appUser.setActivated(false);
        try {
            AppUser createdAppUser = appUserDao.saveAndFlush(appUser);
            userUtil.generateDefaultCategoriesForUser(createdAppUser);
            userUtil.generateAccountRegistration(createdAppUser);
            return new ResponseEntity<>(appUserConverter.convertTo(createdAppUser), HttpStatus.OK);
        } catch (DataIntegrityViolationException dive) {
            throw new ConflictException(dive.getMostSpecificCause().getMessage());
        } catch (MessagingException me) {
            throw new BadRequestException(me.getMessage());
        }
    }
    
    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<AppUserDTO> findAppUser(@PathVariable("id") Long id) {
    
        AppUser appUser = appUserDao.findOne(id);
        if (appUser == null) {
            throw new NotFoundException("User not found");
        }
        return new ResponseEntity<>(appUserConverter.convertTo(appUser), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization") String authorization) {
    
        boolean removed = false;
        String clientIpAddress = ControllerUtil.getRequestClienIpAddress();
        if (clientIpAddress != null) {
            removed = sessionService.removeAuthenticatedSession(authorization, clientIpAddress);
        }
        if (removed) {
            return new ResponseEntity<HttpStatus>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User is not logged in", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<?> login(@RequestHeader(value = "Authorization") String authorization) {
    
        String clientIpAddress = ControllerUtil.getRequestClienIpAddress();
        if (clientIpAddress == null) {
            return new ResponseEntity<>("Invalid request IP address", HttpStatus.BAD_REQUEST);
        }
        
        String authenticationUsername = null;
        String authenticationPassword = null;
        String[] authenticationValues = sessionService.extractUsernameAndPassword(authorization);
        if (authenticationValues.length == 2) {
            authenticationUsername = authenticationValues[0];
            authenticationPassword = authenticationValues[1];
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.BAD_REQUEST);
        }
        
        if (authenticationUsername == null) {
            return new ResponseEntity<>("Invalid username/email provided", HttpStatus.BAD_REQUEST);
        }
        if (authenticationPassword == null) {
            return new ResponseEntity<>("Invalid password", HttpStatus.BAD_REQUEST);
        }
        AppUser appUser = null;
        if (emailValidator.validate(authenticationUsername)) {
            appUser = appUserDao.findByEmail(authenticationUsername);
        } else {
            appUser = appUserDao.findByUsername(authenticationUsername);
        }
        if (appUser == null) {
            throw new NotFoundException("User not found");
        }
        if (!appUser.isActivated()) {
            throw new BadRequestException("User not activated");
        }
        String passwordToLogin = passwordEncrypt.encryptPassword(authenticationPassword);
        if (passwordToLogin.equals(appUser.getPassword())) {
            handleSuccessfulLogin(appUser, authorization, clientIpAddress);
            return new ResponseEntity<>(appUserConverter.convertTo(appUser), HttpStatus.OK);
        } else {
            throw new BadRequestException("Incorrect password");
        }
    }

    @RequestMapping(value = "/change_password", method = RequestMethod.POST)
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePassDTO) {

        String op = changePassDTO.getOp();
        String np = changePassDTO.getNp();
        if (op == null || op.isEmpty() || np == null || np.isEmpty()) {
            throw new BadRequestException("Invalid request!");
        } else if (np.length() < 8) {
            throw new BadRequestException("Password must have at least 8 characters!");
        }
        String oldPassEncrypted = passwordEncrypt.encryptPassword(op);

        AppUser loggedUser = userUtil.extractLoggedAppUserFromDatabase();
        if (!loggedUser.getPassword().equals(oldPassEncrypted)) {
            throw new BadRequestException("Invalid parameters!");
        }

        String newPassEncrypted = passwordEncrypt.encryptPassword(np);
        loggedUser.setPassword(newPassEncrypted);
        appUserDao.saveAndFlush(loggedUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/forgot_password", method = RequestMethod.POST)
    public ResponseEntity<?> sendForgotPasswordMail(@RequestBody ForgotPasswordDTO forgotPassDTO) {

        String email = forgotPassDTO.getEmail();
        if (email == null || email.isEmpty()) {
            throw new BadRequestException("Invalid request!");
        } else if (!emailValidator.validate(email)) {
            throw new BadRequestException("Invalid email address!");
        }

        AppUser appUser = appUserDao.findByEmail(email);
        if (appUser == null) {
            throw new BadRequestException("Invalid request attempt!");
        }

        try {
            userUtil.generateForgotPassword(appUser);
        } catch (MessagingException me) {
            throw new BadRequestException(me.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/renew_forgot_password", method = RequestMethod.POST)
    public ResponseEntity<?> renewForgotPassword(@RequestBody RenewForgotPasswordDTO renewForgotPassDTO) {

        String code = renewForgotPassDTO.getCode();
        String newPassword = renewForgotPassDTO.getNp();
        if (code == null || code.isEmpty()) {
            throw new BadRequestException("Invalid request!");
        } else if (newPassword == null || newPassword.isEmpty()) {
            throw new BadRequestException("Invalid request!");
        } else if (newPassword.length() < 8) {
            throw new BadRequestException("Password should have at least 8 characters!");
        }

        ForgotPassword forgotPassword = forgotPasswordDao.findByCode(code);
        if (forgotPassword == null) {
            throw new BadRequestException("Invalid renew attempt!");
        }

        AppUser appUser = forgotPassword.getUser();
        if (appUser == null) {
            throw new BadRequestException("Invalid user!");
        }

        String newPasswordEncrypted = passwordEncrypt.encryptPassword(newPassword);
        appUser.setPassword(newPasswordEncrypted);
        appUserDao.saveAndFlush(appUser);

        forgotPasswordDao.delete(forgotPassword);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> updateAppUser(@PathVariable("id") Long id, @RequestBody @Valid AppUser appUser) {
    
        AppUser oldAppUser = appUserDao.findOne(id);
        if (oldAppUser == null) {
            throw new NotFoundException("User not found");
        }
        appUser.setUserId(id);
        appUserDao.saveAndFlush(appUser);
        return new ResponseEntity<>("User updated", HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/default_currency", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> setDefaultCurrency(@RequestBody DefaultCurrencyDTO defaultCurrencyDTO) {

        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        String defaultCurrencyValue = defaultCurrencyDTO.getValue();
        if (defaultCurrencyValue == null || defaultCurrencyValue.isEmpty()) {
            throw new BadRequestException("Default currency should not be null!");
        }

        try {
            user.setDefaultCurrency(Currency.getInstance(defaultCurrencyValue));
        } catch (Exception e) {
            throw new BadRequestException("Invalid currency!");
        }
        return new ResponseEntity<DefaultCurrencyDTO>(HttpStatus.OK);
    }

    @RequestMapping(value = "/default_currency", method = RequestMethod.GET)
    public ResponseEntity<?> getDefaultCurrency() {

        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        String default_currency = null;
        if (user.getDefaultCurrency() != null) {
            default_currency = user.getDefaultCurrency().getCurrencyCode();
        }
        DefaultCurrencyDTO defaultCurrencyDTO = new DefaultCurrencyDTO(default_currency);

        return new ResponseEntity<>(defaultCurrencyDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/activation/{code:.+}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<AppUserDTO> activateUser(@PathVariable("code") String code) {
    
        UserRegistration userRegistration = userRegistrationDao.findByCode(code);
        if (userRegistration == null) {
            throw new BadRequestException("Invalid registration code");
        } else {
            AppUser user = userRegistration.getUser();
            user.setActivated(true);
            appUserDao.saveAndFlush(user);
            userRegistrationDao.delete(userRegistration);
            return new ResponseEntity<>(appUserConverter.convertTo(user), HttpStatus.OK);
        }
    }
    
    /**
     * Register session details for the current user logged.
     *
     * @param appUser : currently user logged.
     * @param clientIpAddress
     *      the HTTP request IP address
     * @param authorizationString
     *      the basic authentication string for the current user
     */
    private void handleSuccessfulLogin(AppUser appUser, String authorizationString, String clientIpAddress) {
    
        Timestamp expirationTime = sessionService.calculateExpirationTimeStartingFromNow();
        sessionService.addAuthenticatedSession(new AuthenticatedSession(authorizationString, appUser.getUsername(), clientIpAddress, expirationTime));
    }
}
