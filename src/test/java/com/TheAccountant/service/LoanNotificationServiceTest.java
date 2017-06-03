package com.TheAccountant.service;

import com.TheAccountant.dao.AppUserDao;
import com.TheAccountant.dao.CounterpartyDao;
import com.TheAccountant.dao.LoanDao;
import com.TheAccountant.dao.NotificationDao;
import com.TheAccountant.model.counterparty.Counterparty;
import com.TheAccountant.model.loan.Loan;
import com.TheAccountant.model.notification.Notification;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.service.impl.LoanNotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by tudor.grigoriu on 5/27/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
public class LoanNotificationServiceTest {

    private static final String COUNTERPARTY_NAME = "COUNTERPARTY_NAME";
    private static final String EMAIL = "EMAIL";
    private static final String UPDATED_NAME = "UPDATED_COUNTERPARTY_NAME";
    private static final Double AMOUNT = Double.valueOf("2");
    private static final boolean ACTIVE = true;
    private static final String CURRENCY = "RON";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final Boolean RECEIVING = true;
    private AppUser appUser;
    @Autowired
    private AppUserDao appUserDao;
    @Autowired
    private LoanDao loanDao;
    @Autowired
    private NotificationDao notificationDao;
    @Autowired
    private LoanNotificationService loanNotificationService;
    @Autowired
    private CounterpartyDao counterpartyDao;

    @Before
    public void initialize(){
        appUser = createAppUser("test@my-money-tracker.ro", "user1");
    }

    @Test
    public void shouldAddLoanNotification(){
        Loan loan = createLoan();
        loanDao.save(loan);
        loanNotificationService.addLoanNotifications();
        List<Notification> notifications = notificationDao.fetchAll(appUser.getUsername());
        assertTrue(!notifications.isEmpty());
     }

    private Loan createLoan() {
        Loan loan = new Loan();
        loan.setUser(appUser);
        loan.setCreationDate(new Timestamp(1l));
        loan.setUntilDate(new Timestamp(1l));
        loan.setActive(ACTIVE);
        loan.setAmount(AMOUNT);
        loan.setCurrency(CURRENCY);
        loan.setDescription(DESCRIPTION);
        loan.setReceiving(RECEIVING);
        loan.setCounterparty(createCounterparty());
        return loan;
    }

    private Counterparty createCounterparty() {
        Counterparty counterparty = new Counterparty();
        counterparty.setUser(appUser);
        counterparty.setName(COUNTERPARTY_NAME);
        counterparty.setEmail(EMAIL);
        counterpartyDao.save(counterparty);
        return counterparty;
    }

    private AppUser createAppUser(String email, String username) {

        AppUser appUser = new AppUser();
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setUsername(username);
        appUser.setBirthdate(new Date());
        appUser.setEmail(email);
        appUser.setDefaultCurrency(Currency.getInstance("USD"));
        appUserDao.save(appUser);
        return appUser;
    }
}
