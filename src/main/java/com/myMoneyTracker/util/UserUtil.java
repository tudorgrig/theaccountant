package com.myMoneyTracker.util;

import java.util.UUID;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;

import com.myMoneyTracker.dao.UserRegistrationDao;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.model.user.UserRegistration;

/**
 * Class containing useful methods for User management.
 * 
 * @author Florin Iacob
 */
public class UserUtil {
    
    @Autowired
    private UserRegistrationDao userRegistrationDao;
    
    @Autowired
    private EmailSender emailSender;
    
    /**
     * Method that will generate and send a registration code to the specified user email.
     * 
     * @param user : currently registered user.
     * 
     * @throws MessagingException
     *              exception that is thrown in case of an invalid email address.
     */
    public void generateAccountRegistration(AppUser user) throws MessagingException {
        String code = UUID.randomUUID().toString();
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setCode(code);
        userRegistration.setUser(user);
        userRegistrationDao.save(userRegistration);
        emailSender.sendUserRegistrationEmail(user, code);
    }
}
