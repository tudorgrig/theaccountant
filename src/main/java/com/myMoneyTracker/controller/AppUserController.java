package com.myMoneyTracker.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myMoneyTracker.controller.exception.BadRequestException;
import com.myMoneyTracker.controller.exception.ConflictException;
import com.myMoneyTracker.controller.exception.NotFoundException;
import com.myMoneyTracker.converter.AppUserConverter;
import com.myMoneyTracker.dao.AppUserDao;
import com.myMoneyTracker.dao.UserRegistrationDao;
import com.myMoneyTracker.dto.user.AppUserDTO;
import com.myMoneyTracker.model.session.AuthenticatedSession;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.model.user.UserRegistration;
import com.myMoneyTracker.service.SessionService;
import com.myMoneyTracker.util.ControllerUtil;
import com.myMoneyTracker.util.EmailValidator;
import com.myMoneyTracker.util.PasswordEncrypt;
import com.myMoneyTracker.util.UserUtil;

/**
 * Rest Controller for AppUser entity.
 *
 * @author Floryn
 */
@RestController
@RequestMapping(value = "/user")
public class AppUserController {
    
    @Autowired
    private AppUserDao appUserDao;
    
    @Autowired
    private UserRegistrationDao userRegistrationDao;
    
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
            userUtil.generateAccountRegistration(createdAppUser);
            return new ResponseEntity<AppUserDTO>(appUserConverter.convertTo(createdAppUser), HttpStatus.OK);
        } catch (DataIntegrityViolationException dive) {
            throw new ConflictException(dive.getMostSpecificCause().getMessage());
        } catch (MessagingException me) {
            throw new BadRequestException(me.getMessage());
        }
    }
    
    @RequestMapping(value = "/find_all", method = RequestMethod.GET)
    public ResponseEntity<List<AppUserDTO>> listAllUsers() {
    
        List<AppUser> users = appUserDao.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<List<AppUserDTO>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<AppUserDTO>>(getListOfAppUserDTOs(users), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<AppUserDTO> findAppUser(@PathVariable("id") Long id) {
    
        AppUser appUser = appUserDao.findOne(id);
        if (appUser == null) {
            throw new NotFoundException("User not found");
        }
        return new ResponseEntity<AppUserDTO>(appUserConverter.convertTo(appUser), HttpStatus.OK);
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
            return new ResponseEntity<String>("User is not logged in", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<?> login(@RequestHeader(value = "Authorization") String authorization) {
    
        String clientIpAddress = ControllerUtil.getRequestClienIpAddress();
        if (clientIpAddress == null) {
            return new ResponseEntity<String>("Invalid request IP address", HttpStatus.BAD_REQUEST);
        }
        
        String authenticationUsername = null;
        String authenticationPassword = null;
        String[] authenticationValues = sessionService.extractUsernameAndPassword(authorization);
        if (authenticationValues.length == 2) {
            authenticationUsername = authenticationValues[0];
            authenticationPassword = authenticationValues[1];
        } else {
            return new ResponseEntity<String>("Invalid credentials", HttpStatus.BAD_REQUEST);
        }
        
        if (authenticationUsername == null) {
            return new ResponseEntity<String>("Invalid username/email provided", HttpStatus.BAD_REQUEST);
        }
        if (authenticationPassword == null) {
            return new ResponseEntity<String>("Invalid password", HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<AppUserDTO>(appUserConverter.convertTo(appUser), HttpStatus.OK);
        } else {
            throw new BadRequestException("Incorrect password");
        }
    }
    
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> updateAppUser(@PathVariable("id") Long id, @RequestBody @Valid AppUser appUser) {
    
        AppUser oldAppUser = appUserDao.findOne(id);
        if (oldAppUser == null) {
            throw new NotFoundException("User not found");
        }
        appUser.setId(id);
        appUserDao.saveAndFlush(appUser);
        return new ResponseEntity<String>("User updated", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteAppUser(@PathVariable("id") Long id) {
    
        try {
            appUserDao.delete(id);
            appUserDao.flush();
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new NotFoundException(emptyResultDataAccessException.getMessage());
        }
        return new ResponseEntity<String>("User deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete_all", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteAll() {
    
        appUserDao.deleteAll();
        appUserDao.flush();
        return new ResponseEntity<String>("Users deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/registration/{code:.+}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<AppUserDTO> registerUser(@PathVariable("code") String code) {
    
        UserRegistration userRegistration = userRegistrationDao.findByCode(code);
        if (userRegistration == null) {
            throw new BadRequestException("Invalid registration code");
        } else {
            AppUser user = userRegistration.getUser();
            user.setActivated(true);
            appUserDao.saveAndFlush(user);
            userRegistrationDao.delete(userRegistration);
            return new ResponseEntity<AppUserDTO>(appUserConverter.convertTo(user), HttpStatus.OK);
        }
    }
    
    /**
     * Register session details for the current user logged.
     *
     * @param appUser : currently user logged.
     * @param clientIpAddress
     *      the HTTP request IP address
     * @param authenticationString
     *      the basic authentication string for the current user
     */
    private void handleSuccessfulLogin(AppUser appUser, String authorizationString, String clientIpAddress) {
    
        Timestamp expirationTime = sessionService.calculateExpirationTimeStartingFromNow();
        sessionService.addAuthenticatedSession(new AuthenticatedSession(authorizationString, appUser.getUsername(), clientIpAddress, expirationTime));
    }
    
    private List<AppUserDTO> getListOfAppUserDTOs(List<AppUser> users) {
    
        List<AppUserDTO> appUserDTOs = new ArrayList<AppUserDTO>();
        for (AppUser appUser : users) {
            appUserDTOs.add(appUserConverter.convertTo(appUser));
        }
        return appUserDTOs;
    }
}
