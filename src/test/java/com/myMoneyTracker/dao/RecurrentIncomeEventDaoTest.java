package com.myMoneyTracker.dao;

import com.myMoneyTracker.model.income.Income;
import com.myMoneyTracker.model.income.RecurrentIncomeEvent;
import com.myMoneyTracker.model.user.AppUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Created by tudor.grigoriu on 22.04.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
public class RecurrentIncomeEventDaoTest {


    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private RecurrentIncomeEventDao recurrentIncomeEventDao;

    private static final Logger logger = Logger.getLogger(IncomeDaoTest.class.getName());

    private static final String USERNAME = "DerbedeiidinBacau";
    private static final String EMAIL = "help.mmt@gmail.com";

    private AppUser appUser = null;
    private Income income = null;

    @Before
    public void initialize() {

        appUser = createAppUser(EMAIL, USERNAME);
        income = createIncome(appUser);
    }

    /**
     * Test that should create a recurrent expense event that is repeated every week, on the same week day as new Date()
     */
    @Test
    public void shouldSave(){
        RecurrentIncomeEvent recurrentExpenseEvent = createRecurrentIncomeEvent();
        recurrentIncomeEventDao.save(recurrentExpenseEvent);
        assertTrue(recurrentExpenseEvent.getId() > 0);
        assertNotNull(recurrentExpenseEvent.getIncome());
        assertNotNull(recurrentExpenseEvent.getStartDay());
        assertNotNull(recurrentExpenseEvent.getStartMonth());
    }

    @Test
    public void shouldFindOne(){
        RecurrentIncomeEvent recurrentExpenseEvent = createRecurrentIncomeEvent();
        recurrentIncomeEventDao.save(recurrentExpenseEvent);
        RecurrentIncomeEvent found = recurrentIncomeEventDao.findOne(recurrentExpenseEvent.getId());
        assertNotNull(found);
        assertEquals(recurrentExpenseEvent, found);
    }

    @Test
    public void shouldFindAll(){
        RecurrentIncomeEvent recurrentExpenseEvent1 = createRecurrentIncomeEvent();
        recurrentIncomeEventDao.save(Arrays.asList(recurrentExpenseEvent1));
        List<RecurrentIncomeEvent> found = recurrentIncomeEventDao.findAll();
        assertEquals(1, found.size());
    }

    @Test
    public void shouldDelete(){
        RecurrentIncomeEvent recurrentExpenseEvent1 = createRecurrentIncomeEvent();
        recurrentIncomeEventDao.save(recurrentExpenseEvent1);
        recurrentIncomeEventDao.delete(recurrentExpenseEvent1);
        RecurrentIncomeEvent found = recurrentIncomeEventDao.findOne(recurrentExpenseEvent1.getId());
        assertNull(found);
    }

    private RecurrentIncomeEvent createRecurrentIncomeEvent() {
        RecurrentIncomeEvent recurrentExpenseEvent = new RecurrentIncomeEvent();
        recurrentExpenseEvent.setIncome(income);
        recurrentExpenseEvent.setStartDay(14);
        recurrentExpenseEvent.setStartMonth(1);
        recurrentExpenseEvent.setFrequency("*");
        return recurrentExpenseEvent;
    }


    private Income createIncome(AppUser appUser) {

        Income income = new Income();
        income.setName("name1");
        income.setDescription("description1");
        income.setAmount(new Double(222.222));
        income.setCurrency("USD");
        income.setCreationDate(new Timestamp(System.currentTimeMillis()));
        income.setUser(appUser);
        incomeDao.save(income);
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
