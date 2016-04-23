package com.myMoneyTracker.service;

import com.myMoneyTracker.dao.*;
import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.expense.RecurrentExpenseEvent;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.service.impl.RecurrentEventService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by tudor.grigoriu on 23.04.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
public class RecurrentEventServiceTest {

    @Autowired
    private RecurrentEventService recurrentEventService;

    @Autowired
    private RecurrentExpenseEventDao recurrentExpenseEventDao;

    @Autowired
    private RecurrentIncomeEventDao recurrentIncomeEventDao;

    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private CategoryDao categoryDao;

    private AppUser applicationUser = null;

    private Category category = null;

    @Before
    public void initialize() {

        applicationUser = createAppUser("test@my-money-tracker.ro", "user1");
        category = createCategory(applicationUser);
    }

    @Test
    public void shouldAddRecurrentExpenseOnExactDay(){
        Expense expense = createExpense();
        RecurrentExpenseEvent recurrentExpenseEvent = createRecurrentExpenseEventWithMontlyFrequency(expense);
        assertTrue(recurrentExpenseEvent.getId() > 0);
        recurrentEventService.addRecurrentExpenseEvents();
        List<Expense> expenses = expenseDao.findByUsername(expense.getUser().getUsername());
        assertEquals(2, expenses.size());
    }

    private RecurrentExpenseEvent createRecurrentExpenseEventWithMontlyFrequency(Expense expense) {
        RecurrentExpenseEvent recurrentExpenseEvent = new RecurrentExpenseEvent();
        recurrentExpenseEvent.setExpense(expense);
        recurrentExpenseEvent.setFrequency("*");
        recurrentExpenseEvent.setStartDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        recurrentExpenseEvent.setStartMonth(Calendar.getInstance().get(Calendar.MONTH)+1);
        recurrentExpenseEventDao.save(recurrentExpenseEvent);
        return recurrentExpenseEvent;
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

    private Category createCategory(AppUser currentUser) {

        Category category = new Category();
        category.setName("Florin");
        category.setUser(currentUser);
        categoryDao.save(category);
        return category;
    }
}
