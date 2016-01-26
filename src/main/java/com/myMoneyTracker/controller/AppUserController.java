package com.myMoneyTracker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myMoneyTracker.converter.AppUserConverter;
import com.myMoneyTracker.dao.AppUserDao;
import com.myMoneyTracker.dao.UserRegistrationDao;
import com.myMoneyTracker.dto.user.AppUserDTO;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.model.user.UserRegistration;
import com.myMoneyTracker.util.EmailValidator;
import com.myMoneyTracker.util.PasswordEncrypt;
import com.myMoneyTracker.util.UserUtil;

/**
 * @author Tudor Grigoriu
 *         Rest Controller for AppUser entity
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
    
    private static final Logger log = Logger.getLogger(AppUserController.class.getName());
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> createAppUser(@RequestBody @Valid AppUser appUser) {
    
        String encryptedPassword = passwordEncrypt.encryptPassword(appUser.getPassword());
        appUser.setPassword(encryptedPassword);
        try {
            AppUser createdAppUser = appUserDao.saveAndFlush(appUser);
            userUtil.generateAccountRegistration(createdAppUser);
            return new ResponseEntity<AppUserDTO>(appUserConverter.convertTo(createdAppUser), HttpStatus.OK);
        } catch (DataIntegrityViolationException dive) {
            log.log(Level.INFO, dive.getMessage());
            return new ResponseEntity<String>(dive.getMostSpecificCause().getMessage(), HttpStatus.CONFLICT);
        } catch (MessagingException me) {
            log.log(Level.INFO, me.getMessage());
            return new ResponseEntity<String>(me.getMessage(), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> findAppUser(@PathVariable("id") Long id) {
    
        AppUser appUser = appUserDao.findOne(id);
        if (appUser == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<AppUserDTO>(appUserConverter.convertTo(appUser), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody AppUser userToLogin) {
        if (userToLogin.getUsername() == null) {
            return new ResponseEntity<Object>("Invalid username/email provided", HttpStatus.BAD_REQUEST);
        }
        if (userToLogin.getPassword() == null) {
            return new ResponseEntity<Object>("Invalid password", HttpStatus.BAD_REQUEST);
        }
        AppUser appUser = null;
        if (emailValidator.validate(userToLogin.getUsername())) {
            appUser = appUserDao.findByEmail(userToLogin.getUsername());
        } else {
            appUser = appUserDao.findByUsername(userToLogin.getUsername());
        }
        if (appUser == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }
        if(!appUser.isActivated()){
            return new ResponseEntity<String>("User not activated", HttpStatus.BAD_REQUEST);
        }
        String passwordToLogin = passwordEncrypt.encryptPassword(userToLogin.getPassword());
        if (passwordToLogin.equals(appUser.getPassword())) {
            return new ResponseEntity<AppUserDTO>(appUserConverter.convertTo(appUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Incorrect password", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> updateAppUser(@PathVariable("id") Long id, @RequestBody @Valid AppUser appUser) {
    
        AppUser oldAppUser = appUserDao.findOne(id);
        if (oldAppUser == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }
        appUser.setId(id);
        appUserDao.saveAndFlush(appUser);
        return new ResponseEntity<String>("User updated", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAppUser(@PathVariable("id") Long id) {
    
        try {
            appUserDao.delete(id);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            log.info(emptyResultDataAccessException.getMessage());
            return new ResponseEntity<String>(emptyResultDataAccessException.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>("User deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/deleteAll", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAll() {
    
        appUserDao.deleteAll();
        appUserDao.flush();
        return new ResponseEntity<String>("Users deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/registration/{code:.+}", method = RequestMethod.GET)
    public ResponseEntity<?> registerUser(@PathVariable("code") String code) {
    
        UserRegistration userRegistration = userRegistrationDao.findByCode(code);
        if (userRegistration == null) {
            return new ResponseEntity<String>("Invalid registration code", HttpStatus.BAD_REQUEST);
        } else {
            AppUser user = userRegistration.getUser();
            user.setActivated(true);
            appUserDao.saveAndFlush(user);
            userRegistrationDao.delete(userRegistration);
            return new ResponseEntity<AppUser>(user, HttpStatus.OK);
        }
    }
    
    private List<AppUserDTO> getListOfAppUserDTOs(List<AppUser> users) {
    
        List<AppUserDTO> appUserDTOs = new ArrayList<AppUserDTO>();
        for (AppUser appUser : users) {
            appUserDTOs.add(appUserConverter.convertTo(appUser));
        }
        return appUserDTOs;
    }
}
