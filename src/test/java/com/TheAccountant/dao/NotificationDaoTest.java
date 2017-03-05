package com.TheAccountant.dao;

import com.TheAccountant.model.notification.Notification;
import com.TheAccountant.model.notification.NotificationCategory;
import com.TheAccountant.model.user.AppUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by tudor.grigoriu on 3/5/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
public class NotificationDaoTest {


    private static final boolean SEEN = true;
    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private AppUserDao appUserDao;

    private static final Logger logger = Logger.getLogger(NotificationDaoTest.class.getName());

    private static final String USERNAME = "DerbedeiidinBacau";
    private static final String EMAIL = "help.mmt@gmail.com";
    private static final String MESSAGE = "MESSAGE";

    @Test
    public void shouldSaveNotification() {

        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Notification notification = createNotification(appUser);
        notification = notificationDao.save(notification);
        logger.info("The notification has id = " + notification.getId());
        assertTrue(notification.getId() != 0);
    }

    @Test
    public void shouldFindByUsername() {

        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Notification notification = createNotification(appUser);
        notification = notificationDao.save(notification);
        assertTrue(notification.getId() != 0);
        List<Notification> notificationList = notificationDao.fetchAll(USERNAME);
        assertEquals(notification.getMessage(), notificationList.get(0).getMessage());
    }

    @Test
    public void shouldFindByUsernameWithPagination() {

        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Notification notification = createNotification(appUser);
        notification = notificationDao.save(notification);
        assertTrue(notification.getId() != 0);
        List<Notification> notificationList = notificationDao.fetchAll(appUser.getUserId(), 10, 0);
        assertEquals(notification.getMessage(), notificationList.get(0).getMessage());
    }

    @Test
    public void shouldNotFindByUsername() {

        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Notification notification = createNotification(appUser);
        notification = notificationDao.save(notification);
        assertTrue(notification.getId() != 0);
        List<Notification> notificationList = notificationDao.fetchAll("wrong_username");
        assertTrue(notificationList.isEmpty());
    }

    @Test
    public void shouldFindBySeen() {

        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Notification notification = createNotification(appUser);
        notification = notificationDao.save(notification);
        assertTrue(notification.getId() != 0);
        List<Notification> notificationList = notificationDao.findBySeen(USERNAME, SEEN);
        assertEquals(notification.getMessage(), notificationList.get(0).getMessage());
    }

    @Test
    public void shouldNotFindBySeen() {

        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Notification notification = createNotification(appUser);
        notification = notificationDao.save(notification);
        assertTrue(notification.getId() != 0);
        List<Notification> notificationList = notificationDao.findBySeen(USERNAME, false);
        assertTrue(notificationList.isEmpty());
    }

    @Test
    public void shouldUpdate() {

        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Notification notification = createNotification(appUser);
        notification = notificationDao.save(notification);
        assertTrue(notification.getId() != 0);
        notification.setMessage("UPDATED");
        notificationDao.save(notification);
        List<Notification> notificationList = notificationDao.fetchAll(USERNAME);
        assertEquals("UPDATED", notificationList.get(0).getMessage());
    }

    @Test
    public void shouldDelete() {

        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Notification notification = createNotification(appUser);
        notification = notificationDao.save(notification);
        assertTrue(notification.getId() != 0);
        notificationDao.delete(notification);
        List<Notification> notificationList = notificationDao.fetchAll(USERNAME);
        assertTrue(notificationList.isEmpty());
    }

    private Notification createNotification(AppUser appUser) {
        long creationTimeMillis = System.currentTimeMillis();
        Timestamp creationDate = new Timestamp(creationTimeMillis);
        Notification notification = new Notification();
        notification.setMessage(MESSAGE);
        notification.setCategory(NotificationCategory.LOAN.name());
        notification.setCreationDate(creationDate);
        notification.setSeen(SEEN);
        notification.setUser(appUser);
        return notification;
    }

    private AppUser createAppUser(String email, String username) {

        AppUser appUser = new AppUser();
        appUser.setFirstName("DerbedeiidinBacau");
        appUser.setSurname("DerbedeiidinBacau");
        appUser.setPassword("DerbedeiidinBacau");
        appUser.setUsername(username);
        appUser.setBirthdate(new Date());
        appUser.setEmail(email);
        appUserDao.save(appUser);
        return appUser;
    }
}
