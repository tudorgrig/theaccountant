package com.myMoneyTracker.controller;

import com.myMoneyTracker.dao.AppUserDao;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.EmailValidator;
import com.myMoneyTracker.util.PasswordEncrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tudor Grigoriu
 * Rest Controller for AppUser entity
 */
@RestController
@RequestMapping(value = "/user")
public class AppUserController {

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private EmailValidator emailValidator;

    @Autowired
    private PasswordEncrypt passwordEncrypt;

    private static final Logger log = Logger.getLogger(AppUserController.class.getName());

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> createAppUser(@RequestBody @Valid AppUser appUser) {

        String encryptedPassword = passwordEncrypt.encryptPassword(appUser.getPassword());
        appUser.setPassword(encryptedPassword);
        try {
            AppUser createdAppUser = appUserDao.saveAndFlush(appUser);
            return new ResponseEntity<AppUser>(createdAppUser, HttpStatus.OK);
        } catch (DataIntegrityViolationException dive) {
            log.log(Level.INFO, dive.getMessage());
            return new ResponseEntity<String>(dive.getMostSpecificCause().getMessage(), HttpStatus.CONFLICT);
        }

    }

    @RequestMapping(value = "/find_all", method = RequestMethod.GET)
    public ResponseEntity<List<AppUser>> listAllUsers() {

        List<AppUser> users = appUserDao.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<List<AppUser>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<AppUser>>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> findAppUser(@PathVariable("id") Long id) {

        AppUser appUser = appUserDao.findOne(id);
        if (appUser == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<AppUser>(appUser, HttpStatus.OK);
    }

    @RequestMapping(value = "/login/{login}", method = RequestMethod.GET)
    public ResponseEntity<?> login(@PathVariable("login") String loginString) {

        AppUser appUser = null;
        if (emailValidator.validate(loginString)) {
            appUser = appUserDao.findByEmail(loginString);
        } else {
            appUser = appUserDao.findByUsername(loginString);
        }
        if (appUser == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<AppUser>(appUser, HttpStatus.OK);
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
        return new ResponseEntity<String>("Users deleted", HttpStatus.NO_CONTENT);
    }
}
