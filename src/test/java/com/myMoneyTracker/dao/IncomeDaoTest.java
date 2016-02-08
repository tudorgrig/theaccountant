package com.myMoneyTracker.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.myMoneyTracker.model.income.Income;
import com.myMoneyTracker.model.user.AppUser;

/**
 *  This class represents the test class for the 'income' data access object
 *
 * @author Florin, on 19.12.2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
public class IncomeDaoTest {
    
    @Autowired
    private IncomeDao incomeDao;
    
    @Autowired
    private AppUserDao appUserDao;
    
    @Autowired
    private CategoryDao categoryDao;
    
    @Autowired
    private UserRegistrationDao userRegistrationDao;
    
    private static final Logger logger = Logger.getLogger(IncomeDaoTest.class.getName());
    
    private static final String USERNAME = "tudorgrig";
    private static final String EMAIL = "help.mmt@gmail.com";
    
    @Before
    public void cleanUp() {
    
        userRegistrationDao.deleteAll();
        userRegistrationDao.flush();
        incomeDao.deleteAll();
        incomeDao.flush();
        appUserDao.deleteAll();
        appUserDao.flush();
    }
    
    @Test
    public void shouldSaveIncome() {
    
        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Income income = createIncome(appUser);
        income = incomeDao.save(income);
        logger.info("The income has id = " + income.getId());
        assertTrue(income.getId() != 0);
    }
    
    @Test
    public void shouldFindIncome() {
    
        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Income income = createIncome(appUser);
        income = incomeDao.save(income);
        income = incomeDao.findOne(income.getId());
        assertTrue(income != null);
    }
    
    @Test
    public void shouldFindIncomeByUserId() {
    
        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Income income = createIncome(appUser);
        income = incomeDao.save(income);
        List<Income> incomeForUser = incomeDao.findByUserId(income.getUser().getId());
        assertEquals(1, incomeForUser.size());
        assertEquals(income.getId(), incomeForUser.get(0).getId());
    }
    
    @Test
    public void shouldNotFindIncome() {
    
        Income income = incomeDao.findOne(new Random().nextLong());
        assertTrue(income == null);
    }
    
    @Test
    public void shouldUpdateIncome() {
    
        String updatedName = "NameUpdated";
        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Income income = createIncome(appUser);
        income = incomeDao.save(income);
        income.setName(updatedName);
        Income result = incomeDao.save(income);
        assertTrue(result.getName().equals(updatedName));
    }
    
    @Test
    public void shouldSaveAndFlush() {
    
        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Income income = createIncome(appUser);
        income = incomeDao.saveAndFlush(income);
        assertTrue(income.getId() > 0);
    }
    
    @Test
    public void shouldFindAll() {
    
        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Income income1 = createIncome(appUser);
        AppUser appUser2 = createAppUser("test@test.com", "test_username");
        Income income2 = createIncome(appUser2);
        incomeDao.save(income1);
        incomeDao.save(income2);
        List<Income> incomeList = incomeDao.findAll();
        assertEquals(2, incomeList.size());
    }
    
    @Test
    public void shouldHaveUserNotNull() {
    
        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Income income = createIncome(appUser);
        income = incomeDao.save(income);
        assertTrue(income.getUser() != null);
    }
    
    private Income createIncome(AppUser appUser) {
    
        Income income = new Income();
        income.setName("name1");
        income.setDescription("description1");
        income.setAmount(new Double(222.222));
        income.setCurrency("USD");
        income.setCreationDate(new Timestamp(System.currentTimeMillis()));
        income.setUser(appUser);
        return income;
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
