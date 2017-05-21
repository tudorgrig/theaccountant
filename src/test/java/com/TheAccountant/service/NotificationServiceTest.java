package com.TheAccountant.service;

import com.TheAccountant.dao.AppUserDao;
import com.TheAccountant.dao.AuthenticatedSessionDao;
import com.TheAccountant.dao.CategoryDao;
import com.TheAccountant.dao.ExpenseDao;
import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.expense.Expense;
import com.TheAccountant.model.notification.Notification;
import com.TheAccountant.model.notification.NotificationPriority;
import com.TheAccountant.model.session.AuthenticatedSession;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.util.ControllerUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.Currency;
import java.util.Date;

/**
 * Test class for {@link NotificationService}
 *
 * @author Florin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
public class NotificationServiceTest {

    private AppUser applicationUser = null;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ExpenseDao expenseDao;

    @Before
    public void setup() {

        applicationUser = createAndSaveAppUser("florin.iacob.expense@gmail.com", "username1");
        ControllerUtil.setCurrentLoggedUser("username1");
    }

    @After
    public void cleanUp() {
        appUserDao.delete(applicationUser.getUserId());
        appUserDao.flush();
    }

    @Test
    public void shouldRegisterThresholdNotificationWithHighPriority() {
        Category category = this.createAndSaveCategory("notif test", 1000.0);
        this.createAndSaveExpense(category, 600);
        this.createAndSaveExpense(category, 500);
        Notification notification = notificationService.registerThresholdNotification(category);
        Assert.assertNotNull(notification);
        Assert.assertEquals(NotificationPriority.HIGH.name(), notification.getPriority());
    }

    @Test
    public void shouldNotRegisterThresholdNotificationWithHighPriority() {
        Category category1 = this.createAndSaveCategory("notif test1", 1000.0);
        Category category2 = this.createAndSaveCategory("notif test2", 0.0);
        this.createAndSaveExpense(category1, 600);
        this.createAndSaveExpense(category2, 500);
        this.createAndSaveExpense(category2, 100000);

        Notification notification = notificationService.registerThresholdNotification(category1);
        Assert.assertNull(notification);

        notification = notificationService.registerThresholdNotification(category2);
        Assert.assertNull(notification);
    }

    private Category createAndSaveCategory(String name, Double threshold) {
        Category category = new Category();
        category.setUser(applicationUser);
        category.setName(name);
        category.setThreshold(threshold);
        category = categoryDao.save(category);
        return  category;
    }

    private Expense createAndSaveExpense(Category category, double amount) {
        Expense expense = new Expense();
        expense.setUser(applicationUser);
        expense.setName("Expense name");
        expense.setCreationDate(new Timestamp(System.currentTimeMillis()));
        expense.setCurrency("EUR");
        expense.setAmount(amount);
        expense.setCategory(category);
        expense = expenseDao.save(expense);
        return expense;
    }

    private AppUser createAndSaveAppUser(String email, String username) {

        AppUser appUser = new AppUser();
        appUser.setFirstName("DerbedeiidinBacau");
        appUser.setSurname("DerbedeiidinBacau");
        appUser.setPassword("DerbedeiidinBacau");
        appUser.setUsername(username);
        appUser.setBirthdate(new Date());
        appUser.setEmail(email);
        appUser.setDefaultCurrency(Currency.getInstance("RON"));
        appUser = appUserDao.save(appUser);
        return appUser;
    }

}
