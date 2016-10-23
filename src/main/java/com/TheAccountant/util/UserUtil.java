package com.TheAccountant.util;

import com.TheAccountant.controller.exception.UnauthorizedException;
import com.TheAccountant.dao.AppUserDao;
import com.TheAccountant.dao.CategoryDao;
import com.TheAccountant.dao.ForgotPasswordDao;
import com.TheAccountant.dao.UserRegistrationDao;
import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.model.user.ForgotPassword;
import com.TheAccountant.model.user.UserRegistration;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;
import java.util.UUID;

/**
 * Class containing useful methods for User management.
 * 
 * @author Florin Iacob
 */
public class UserUtil {
    
    @Autowired
    private UserRegistrationDao userRegistrationDao;

    @Autowired
    private ForgotPasswordDao forgotPasswordDao;
    
    @Autowired
    private EmailSender emailSender;
    
    @Autowired
    private AppUserDao appUserDao;
    
    @Autowired
    private EmailValidator emailValidator;

    @Autowired
    private CategoryDao categoryDao;
    
    /**
     * Method that will generate and send a registration code to the specified user email.
     * 
     * @param user : currently registered user.
     * 
     * @throws MessagingException:
 *              exception that is thrown in case of any issues sending the email
     *          to the user's address
     */
    public void generateAccountRegistration(AppUser user) throws MessagingException {
    
        String code = UUID.randomUUID().toString();
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setCode(code);
        userRegistration.setUser(user);
        userRegistrationDao.saveAndFlush(userRegistration);
        emailSender.sendUserRegistrationEmail(user, code);
    }

    /**
     * Method that will generate and send a code to the specified user email for
     * renewing the forgotten password.
     *
     * @param user : the user that requests the renewal of the password
     * @throws MessagingException:
     *          exception that is thrown in case of any issues sending the email
     *          to the user's address
     */
    public void generateForgotPassword(AppUser user) throws MessagingException {

        String code = UUID.randomUUID().toString();
        ForgotPassword forgotPasswordEntity = new ForgotPassword();
        forgotPasswordEntity.setCode(code);
        forgotPasswordEntity.setUser(user);
        forgotPasswordDao.saveAndFlush(forgotPasswordEntity);
        emailSender.sendForgotPasswordEmail(user, code);
    }

    /**
     * Extracts an appUser from the database based on the authentication string registered
     * on session.
     * The authentication string can be the username or the email of the user. 
     * 
     * @return
     *      the user from the database or throws {@link UnauthorizedException} if the user
     *      cannot be found.
     */
    public AppUser extractLoggedAppUserFromDatabase() {
        String loginString = ControllerUtil.getCurrentLoggedUsername();
        AppUser appUser = null;
        if (emailValidator.validate(loginString)) {
            appUser = appUserDao.findByEmail(loginString);
        } else {
            appUser = appUserDao.findByUsername(loginString);
        }
        if (appUser == null) {
            throw new UnauthorizedException("Unauthorized attempt!");
        }
        return appUser;
    }


    public void generateDefaultCategoriesForUser(AppUser appUser){
        CategoryUtil.DEFAULT_CATEGORIES.forEach(category -> {
            Category clone = category.clone();
            clone.setUser(appUser);
            categoryDao.saveAndFlush(clone);
        });
        categoryDao.flush();
    }
}
