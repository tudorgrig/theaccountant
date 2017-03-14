package com.TheAccountant.dao;

import com.TheAccountant.model.counterparty.Counterparty;
import com.TheAccountant.model.user.AppUser;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Class created to test {@link CounterpartyDao} class
 *
 * Created by Florin on 3/11/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
public class CounterpartyDaoTest {

    private static final Logger logger = Logger.getLogger(CounterpartyDaoTest.class.getName());

    private static final String COUNTERPARTY_EMAIL = "counterparty@theacctnt.com";

    private static final String LOGGED_USERNAME = "counterparty_user";
    private static final String LOGGED_EMAIL = "counterparty_test@my-money-tracker.ro";
    private AppUser loggedUser;

    @Autowired
    private CounterpartyDao counterpartyDao;

    @Autowired
    private AppUserDao appUserDao;

    @Before
    public void initialize() {
        loggedUser = createAppUser(LOGGED_EMAIL, LOGGED_USERNAME);
    }

    @After
    public void clean() {
        appUserDao.delete(loggedUser.getUserId());
    }

    @Test
    public void shouldSaveCounterparty() {

        Counterparty counterparty = createCounterparty(loggedUser, COUNTERPARTY_EMAIL, "John Jones");
        Counterparty dbCounterparty = counterpartyDao.save(counterparty);
        assertNotNull("Saved Counterparty should NOT be null!", dbCounterparty);
        assertTrue(dbCounterparty.getId() != 0);
        assertEquals(counterparty.getName(), dbCounterparty.getName());
        counterpartyDao.delete(dbCounterparty.getId());
    }

    @Test
    public void shouldFetchAllByUsername() {

        Counterparty counterparty1 = createCounterparty(loggedUser, COUNTERPARTY_EMAIL, "John Jones");
        Counterparty dbCounterparty1 = counterpartyDao.save(counterparty1);
        Counterparty counterparty2 = createCounterparty(loggedUser, COUNTERPARTY_EMAIL + "2", "Jackie Chan");
        Counterparty dbCounterparty2 = counterpartyDao.save(counterparty2);
        Counterparty counterparty3 = createCounterparty(loggedUser, COUNTERPARTY_EMAIL + "3", "Donald Trump");
        Counterparty dbCounterparty3 = counterpartyDao.save(counterparty3);

        List<Counterparty> dbQueryResult = counterpartyDao.fetchAll(loggedUser.getUsername());
        assertNotNull("Counterparty List should NOT be NULL when fetching by username", dbQueryResult);
        assertEquals(3, dbQueryResult.size());

        counterpartyDao.delete(dbCounterparty1.getId());
        counterpartyDao.delete(dbCounterparty2.getId());
        counterpartyDao.delete(dbCounterparty3.getId());
    }

    @Test
    public void shouldNotFetchByUsername() {

        Counterparty counterparty = createCounterparty(loggedUser, COUNTERPARTY_EMAIL, "John Jones");
        Counterparty dbCounterparty = counterpartyDao.save(counterparty);

        List<Counterparty> dbQueryResult = counterpartyDao.fetchAll("INVALID_USERNAME");
        assertTrue(dbQueryResult == null || dbQueryResult.size() == 0);

        counterpartyDao.delete(dbCounterparty.getId());
    }

    private Counterparty createCounterparty(AppUser appUser, String email, String name) {
        Counterparty counterparty = new Counterparty();
        counterparty.setUser(appUser);
        counterparty.setEmail(email);
        counterparty.setName(name);
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
        appUserDao.save(appUser);
        return appUser;
    }

}
