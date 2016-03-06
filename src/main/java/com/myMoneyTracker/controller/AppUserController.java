package com.myMoneyTracker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.Valid;

import com.myMoneyTracker.controller.exception.BadRequestException;
import com.myMoneyTracker.controller.exception.ConflictException;
import com.myMoneyTracker.controller.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myMoneyTracker.app.service.SessionAuthentication;
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

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<AppUserDTO> login(@RequestBody AppUser userToLogin) {

        if (userToLogin.getUsername() == null) {
            throw new BadRequestException("Invalid username provided");
        }
        if (userToLogin.getPassword() == null) {
            throw new BadRequestException("Invalid password provided");
        }

        AppUser appUser = null;
        if (emailValidator.validate(userToLogin.getUsername())) {
            appUser = appUserDao.findByEmail(userToLogin.getUsername());
        } else {
            appUser = appUserDao.findByUsername(userToLogin.getUsername());
        }
        if (appUser == null) {
            throw new NotFoundException("User not found");
        }
        if (!appUser.isActivated()) {
            throw new BadRequestException("User not activated");
        }
        String passwordToLogin = passwordEncrypt.encryptPassword(userToLogin.getPassword());
        if (passwordToLogin.equals(appUser.getPassword())) {
            String sessionToken = handleSuccessfulLogin(appUser);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Access-Control-Expose-Headers", "mmtlt");
            headers.add("mmtlt", sessionToken);
            return new ResponseEntity<AppUserDTO>(appUserConverter.convertTo(appUser), headers, HttpStatus.OK);
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
     * @return : generated session token
     */
    private String handleSuccessfulLogin(AppUser appUser) {

        String sessionToken = UUID.randomUUID().toString();
        SessionAuthentication authentication = new SessionAuthentication(appUser, sessionToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return sessionToken;

    }

    private List<AppUserDTO> getListOfAppUserDTOs(List<AppUser> users) {

        List<AppUserDTO> appUserDTOs = new ArrayList<AppUserDTO>();
        for (AppUser appUser : users) {
            appUserDTOs.add(appUserConverter.convertTo(appUser));
        }
        return appUserDTOs;
    }
}
