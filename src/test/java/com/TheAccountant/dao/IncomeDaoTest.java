package com.TheAccountant.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.TheAccountant.model.income.Income;
import com.TheAccountant.model.user.AppUser;

/**
 *  This class represents the test class for the 'income' data access object
 *
 * @author Florin, on 19.12.2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
public class IncomeDaoTest {
    
    @Autowired
    private IncomeDao incomeDao;
    
    @Autowired
    private AppUserDao appUserDao;
    
    private static final Logger logger = Logger.getLogger(IncomeDaoTest.class.getName());
    
    private static final String USERNAME = "DerbedeiidinBacau";
    private static final String EMAIL = "help.mmt@gmail.com";

    
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
        List<Income> incomeForUser = incomeDao.findByUserId(income.getUser().getUserId());
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
        List<Income> incomeList = incomeDao.findByUsername(appUser.getUsername());
        assertEquals(1, incomeList.size());
    }
    
    @Test
    public void shouldHaveUserNotNull() {
    
        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Income income = createIncome(appUser);
        income = incomeDao.save(income);
        assertTrue(income.getUser() != null);
    }

    @Test
    public void shouldFindIncomeInInterval(){
        Timestamp nowTimeStamp = new Timestamp(System.currentTimeMillis());
        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Income income = createIncome(appUser);
        income = incomeDao.save(income);
        Timestamp untilTimeStamp = new Timestamp(System.currentTimeMillis());
        List<Income> incomes = incomeDao.findIncomesInTimeInterval(nowTimeStamp, untilTimeStamp, appUser.getUsername());
        assertEquals(1, incomes.size());
        assertEquals(income, incomes.get(0));
    }

    @Test
    public void shouldNotFindIncomeInInterval() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse("23/09/2007");
        Timestamp timestamp = new Timestamp(date.getTime());
        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Income income = createIncome(appUser);
        income = incomeDao.save(income);
        List<Income> incomes = incomeDao.findIncomesInTimeInterval(timestamp, timestamp, appUser.getUsername());
        assertEquals(0, incomes.size());
    }

    @Test(expected = javax.validation.ConstraintViolationException.class)
    public void shouldNotCreateIncomeWithNegativeAmount(){
        AppUser appUser = createAppUser(EMAIL, USERNAME);
        Income income = createIncome(appUser);
        income.setAmount(Double.valueOf(-1));
        incomeDao.save(income);
        incomeDao.flush();
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
