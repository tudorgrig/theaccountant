package com.TheAccountant.controller;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.dao.AppUserDao;
import com.TheAccountant.dao.NotificationDao;
import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.dto.notification.NotificationDTO;
import com.TheAccountant.model.notification.Notification;
import com.TheAccountant.model.notification.NotificationCategory;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.testUtil.TestMockUtil;
import com.TheAccountant.util.ControllerUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import static com.TheAccountant.controller.PaymentControllerTest.TEST_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by tudor.grigoriu on 3/7/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
public class NotificationControllerTest {

    private static final String LOGGED_USERNAME = "florin.iacob";
    private static final String MESSAGE = "NOTIFICATION MESSAGE";
    private static final boolean SEEN = true;

    private AppUser applicationUser;

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private PaymentController paymentController;

    @Before
    public void setup() {

        applicationUser = createAndSaveAppUser(LOGGED_USERNAME, "florin.iacob.expense@gmail.com");
        ControllerUtil.setCurrentLoggedUser(LOGGED_USERNAME);

        // Only paid accounts can access Notifications Module
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        chargeDTO.setStripeToken(TEST_TOKEN);
        paymentController.charge(chargeDTO);
    }

    @Test
    public void shouldFindNotifications(){
        Notification notification = createNotificationAndSave();
        ResponseEntity<List<NotificationDTO>> responseEntity = notificationController.findNotifications(10, 0);
        List<NotificationDTO> notifications = responseEntity.getBody();
        assertEquals(notification.getMessage(), notifications.get(0).getMessage());
        assertEquals(notification.getCategory(), notifications.get(0).getCategory());
        assertEquals(notification.getCreationDate(), notifications.get(0).getCreationDate());
    }

    @Test
    public void shouldNotFindNotificationsForUnpaidAccount(){
        applicationUser = createAndSaveAppUser("notification_controller_test", "notification_controller_test@gmail.com");
        ControllerUtil.setCurrentLoggedUser("notification_controller_test");

        createNotificationAndSave();
        try {
            notificationController.findNotifications(10, 0);
        } catch (BadRequestException e) {
            assertEquals("Notifications are allowed only for paid accounts!", e.getMessage());
            return;
        }
        fail("Should not allow find notifications for unpaid accounts!");
    }

    @Test
    public void shouldMarkAsSeen(){
        Notification notification = createNotificationAndSave();
        notification.setSeen(false);
        notificationDao.saveAndFlush(notification);
        notificationController.markNotificationAsSeen(notification.getId());
        Notification result = notificationDao.findOne(notification.getId());
        assertTrue(result.isSeen());
    }

    private Notification createNotificationAndSave() {
        long creationTimeMillis = System.currentTimeMillis();
        Timestamp creationDate = new Timestamp(creationTimeMillis);
        Notification notification = new Notification();
        notification.setMessage(MESSAGE);
        notification.setCategory(NotificationCategory.LOAN.name());
        notification.setCreationDate(creationDate);
        notification.setSeen(SEEN);
        notification.setUser(applicationUser);
        notificationDao.saveAndFlush(notification);
        return notification;
    }

    private AppUser createAndSaveAppUser(String username, String email) {

        AppUser appUser = new AppUser();
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setBirthdate(new Date());
        appUser.setUsername(username);
        appUser.setEmail(email);
        appUser.setDefaultCurrency(Currency.getInstance("RON"));
        appUser = appUserDao.saveAndFlush(appUser);
        return appUser;
    }

}
