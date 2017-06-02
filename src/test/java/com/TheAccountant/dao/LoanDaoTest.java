package com.TheAccountant.dao;

import com.TheAccountant.model.counterparty.Counterparty;
import com.TheAccountant.model.loan.Loan;
import com.TheAccountant.model.user.AppUser;
import org.junit.After;
import org.junit.Before;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test Class for {@link LoanDao} class
 *
 * Created by Florin on 3/11/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
public class LoanDaoTest {

    private static final Logger logger = Logger.getLogger(LoanDaoTest.class.getName());

    private static final String COUNTERPARTY_EMAIL = "counterparty@loadDaoTest.com";

    private static final String LOGGED_USERNAME = "loan_user";
    private static final String LOGGED_EMAIL = "logged_user@loadDaoTest.com";
    private AppUser loggedUser;
    private Counterparty defaultCounterparty;

    @Autowired
    private LoanDao loanDao;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private CounterpartyDao counterpartyDao;

    @Before
    public void initialize() {
        loggedUser = createAppUser(LOGGED_EMAIL, LOGGED_USERNAME);
        defaultCounterparty = counterpartyDao.save(createCounterparty(loggedUser, COUNTERPARTY_EMAIL, "counterpartName"));
    }

    @After
    public void clean() {
        counterpartyDao.delete(defaultCounterparty.getId());
        appUserDao.delete(loggedUser.getUserId());
    }

    @Test
    public void shouldSaveLoan() {

        Loan loan = createLoan(loggedUser, defaultCounterparty, true);
        Loan dbLoan = loanDao.save(loan);
        assertNotNull("Saved Loan should NOT be null!", dbLoan);
        assertTrue(dbLoan.getId() != 0);
        assertEquals(loan.getAmount(), dbLoan.getAmount());
        loanDao.delete(dbLoan.getId());
    }

    @Test
    public void shouldFetchAllByUsername() {
        Loan loan1 = createLoan(loggedUser, defaultCounterparty, true);
        Loan dbLoan1 = loanDao.save(loan1);
        Loan loan2 = createLoan(loggedUser, defaultCounterparty, false);
        Loan dbLoan2 = loanDao.save(loan2);

        List<Loan> dbQueryResult = loanDao.fetchAll(loggedUser.getUsername());
        assertNotNull("Loans List should NOT be NULL when fetching by username", dbQueryResult);
        assertEquals(2, dbQueryResult.size());

        List<Loan> dbQueryResultWithOffset = loanDao.fetchAll(loggedUser.getUserId(), 1, 0);
        assertNotNull("Loans List should NOT be NULL when fetching by username with OFFSET", dbQueryResultWithOffset);
        assertEquals(1, dbQueryResultWithOffset.size());

        loanDao.delete(dbLoan1.getId());
        loanDao.delete(dbLoan2.getId());
    }

    @Test
    public void shouldNotFetchAllByUsername() {
        Loan loan = createLoan(loggedUser, defaultCounterparty, true);
        Loan dbLoan = loanDao.save(loan);

        List<Loan> dbQueryResult = loanDao.fetchAll("INVALID_USERNAME");
        assertTrue(dbQueryResult == null || dbQueryResult.size() == 0);

        List<Loan> dbQueryResultWithOffset = loanDao.fetchAll(loggedUser.getUserId() + 1, 1, 0);
        assertTrue(dbQueryResultWithOffset == null || dbQueryResultWithOffset.size() == 0);

        loanDao.delete(dbLoan.getId());
    }

    @Test
    public void shouldFindByActive() {
        Loan loan = createLoan(loggedUser, defaultCounterparty, true);
        Loan dbLoan = loanDao.save(loan);

        List<Loan> dbQueryResult = loanDao.findByActive(loggedUser.getUsername(), true);
        assertNotNull("Loans List should NOT be NULL when fetching by active", dbQueryResult);
        assertEquals(1, dbQueryResult.size());
    }

    @Test
    public void shouldNotFindByActive() {
        Loan loan = createLoan(loggedUser, defaultCounterparty, true);
        Loan dbLoan = loanDao.save(loan);

        List<Loan> dbQueryResult = loanDao.findByActive(loggedUser.getUsername(), false);
        assertTrue(dbQueryResult == null || dbQueryResult.size() == 0);
    }

    @Test
    public void shouldFindByCounterparty() {
        Loan loan = createLoan(loggedUser, defaultCounterparty, true);
        Loan dbLoan = loanDao.save(loan);

        List<Loan> dbQueryResult = loanDao.findByCounterparty(loggedUser.getUsername(), defaultCounterparty.getId());
        assertNotNull("Loans List should NOT be NULL when fetching by counterparty", dbQueryResult);
        assertEquals(1, dbQueryResult.size());
    }

    @Test
    public void shouldNotFindByCounterparty() {
        Loan loan = createLoan(loggedUser, defaultCounterparty, true);
        Loan dbLoan = loanDao.save(loan);

        List<Loan> dbQueryResult = loanDao.findByCounterparty(loggedUser.getUsername(), defaultCounterparty.getId() + 1);
        assertTrue(dbQueryResult == null || dbQueryResult.size() == 0);
    }

    @Test
    public void shouldFindByCounterpartyAndActive() {
        Loan loan = createLoan(loggedUser, defaultCounterparty, true);
        Loan dbLoan = loanDao.save(loan);

        List<Loan> dbQueryResult = loanDao.findByCounterpartyAndActive(loggedUser.getUsername(),
                defaultCounterparty.getId(), true);
        assertNotNull("Loans List should NOT be NULL when fetching by counterparty amd active", dbQueryResult);
        assertEquals(1, dbQueryResult.size());
    }

    @Test
    public void shouldNotFindByCounterpartyAndActive() {
        Loan loan = createLoan(loggedUser, defaultCounterparty, true);
        Loan dbLoan = loanDao.save(loan);

        List<Loan> dbQueryResult = loanDao.findByCounterpartyAndActive(loggedUser.getUsername(),
                defaultCounterparty.getId(), false);
        assertTrue(dbQueryResult == null || dbQueryResult.size() == 0);
    }

    private Loan createLoan(AppUser appUser, Counterparty counterparty, boolean active) {
        Loan loan = new Loan();
        loan.setUser(appUser);
        loan.setCounterparty(counterparty);
        loan.setActive(active);
        loan.setAmount(300.0);
        loan.setCurrency("USD");
        loan.setDescription("Test Loan");
        loan.setCreationDate(new Timestamp(new Date().getTime()));
        loan.setUntilDate(new Timestamp(new Date().getTime()));
        loan.setReceiving(true);
        return loan;
    }

    private AppUser createAppUser(String email, String username) {

        AppUser appUser = new AppUser();
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setUsername(username);
        appUser.setBirthdate(new Date());
        appUser.setEmail(email);
        appUserDao.save(appUser);
        return appUser;
    }

    private Counterparty createCounterparty(AppUser appUser, String email, String name) {
        Counterparty counterparty = new Counterparty();
        counterparty.setUser(appUser);
        counterparty.setEmail(email);
        counterparty.setName(name);
        return counterparty;
    }
}
