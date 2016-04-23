package com.myMoneyTracker.dao;

import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.expense.RecurrentExpenseEvent;
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

import static org.junit.Assert.*;

/**
 * Created by tudor.grigoriu on 22.04.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
public class RecurrentExpenseEventDaoTest {

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private RecurrentExpenseEventDao recurrentExpenseEventDao;

    private static final String USERNAME = "DerbedeiidinBacau";
    private static final String EMAIL = "help.mmt@gmail.com";

    private AppUser applicationUser = null;

    private Category category = null;

    private Expense expense = null;

    @Before
    public void initialize() {

        applicationUser = createAppUser(EMAIL, USERNAME);
        category = createCategory(applicationUser);
        expense = createExpense();
    }

    /**
     * Test that should create a recurrent expense event that is repeated every week, on the same week day as new Date()
     */
    @Test
    public void shouldSave(){
        RecurrentExpenseEvent recurrentExpenseEvent = createRecurrentExpenseEvent();
        recurrentExpenseEventDao.save(recurrentExpenseEvent);
        assertTrue(recurrentExpenseEvent.getId() > 0);
        assertNotNull(recurrentExpenseEvent.getExpense());
        assertNotNull(recurrentExpenseEvent.getStartDay());
        assertNotNull(recurrentExpenseEvent.getStartMonth());
    }

    @Test
    public void shouldFindOne(){
        RecurrentExpenseEvent recurrentExpenseEvent = createRecurrentExpenseEvent();
        recurrentExpenseEventDao.save(recurrentExpenseEvent);
        RecurrentExpenseEvent found = recurrentExpenseEventDao.findOne(recurrentExpenseEvent.getId());
        assertNotNull(found);
        assertEquals(recurrentExpenseEvent, found);
    }

    @Test
    public void shouldFindAll(){
        RecurrentExpenseEvent recurrentExpenseEvent1 = createRecurrentExpenseEvent();
        recurrentExpenseEventDao.save(Arrays.asList(recurrentExpenseEvent1));
        List<RecurrentExpenseEvent> found = recurrentExpenseEventDao.findAll();
        assertEquals(1, found.size());
    }

    @Test
    public void shouldDelete(){
        RecurrentExpenseEvent recurrentExpenseEvent1 = createRecurrentExpenseEvent();
        recurrentExpenseEventDao.save(recurrentExpenseEvent1);
        recurrentExpenseEventDao.delete(recurrentExpenseEvent1);
        RecurrentExpenseEvent found = recurrentExpenseEventDao.findOne(recurrentExpenseEvent1.getId());
        assertNull(found);
    }

    private RecurrentExpenseEvent createRecurrentExpenseEvent() {
        RecurrentExpenseEvent recurrentExpenseEvent = new RecurrentExpenseEvent();
        recurrentExpenseEvent.setExpense(expense);
        recurrentExpenseEvent.setStartDay(8);
        recurrentExpenseEvent.setStartMonth(10);
        recurrentExpenseEvent.setFrequency("*");
        return recurrentExpenseEvent;
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

    private Category createCategory(AppUser currentUser) {

        Category category = new Category();
        category.setName("Florin");
        category.setUser(currentUser);
        categoryDao.save(category);
        return category;
    }

    private Expense createExpense() {

        Expense expense = new Expense();
        expense.setName("name1");
        expense.setCurrency("USD");
        expense.setDescription("description1");
        expense.setAmount(new Double(222.222));
        expense.setCreationDate(new Timestamp(System.currentTimeMillis()));
        expense.setUser(applicationUser);
        expense.setCategory(category);
        expenseDao.save(expense);
        return expense;
    }
}
