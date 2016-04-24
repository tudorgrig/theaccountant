package com.myMoneyTracker.service;

import com.myMoneyTracker.dao.*;
import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.income.Income;
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

    private static final int TWO_MONTH = 2;
    private static final int QUARTERLY = 3;

    @Autowired
    private RecurrentEventService recurrentEventService;

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
        Expense expense = createExpenseWithMonthlyFrequency();
        assertTrue(expense.getId() > 0);
        recurrentEventService.addRecurrentExpenseEvents();
        List<Expense> expenses = expenseDao.findByUsername(expense.getUser().getUsername());
        assertEquals(2, expenses.size());
    }

    @Test
    public void shouldAddRecurrentIncomeOnExactDay(){
        Income income = createIncomeWithMonthlyFrequency();
        assertTrue(income.getId() > 0);
        recurrentEventService.addRecurrentIncomeEvents();
        List<Income> incomes = incomeDao.findByUsername(income.getUser().getUsername());
        assertEquals(2, incomes.size());
    }

    @Test
    public void shouldAddRecurrentExpenseOn2MonthFrequency(){
        Expense expense = createExpenseWith2MonthFrequency();
        assertTrue(expense.getId() > 0);
        recurrentEventService.addRecurrentExpenseEvents();
        List<Expense> expenses = expenseDao.findByUsername(expense.getUser().getUsername());
        assertEquals(2, expenses.size());
    }

    @Test
    public void shouldAddRecurrentIncomeOn2MonthFrequency(){
        Income income = createIncomeWith2MonthFrequency();
        assertTrue(income.getId() > 0);
        recurrentEventService.addRecurrentIncomeEvents();
        List<Income> incomes = incomeDao.findByUsername(income.getUser().getUsername());
        assertEquals(2, incomes.size());
    }

    @Test
    public void shouldAddRecurrentExpenseOnQuarterlyFrequency(){
        Expense expense = createExpenseWithQuarterlyFrequency();
        assertTrue(expense.getId() > 0);
        recurrentEventService.addRecurrentExpenseEvents();
        List<Expense> expenses = expenseDao.findByUsername(expense.getUser().getUsername());
        assertEquals(2, expenses.size());
    }

    @Test
    public void shouldNotAddRecurrentExpenseOnQuarterlyFrequency(){
        Expense expense = createExpenseWithQuarterlyFrequency();
        expense.setStartMonth(expense.getStartMonth() + 1);
        expenseDao.save(expense);
        assertTrue(expense.getId() > 0);
        recurrentEventService.addRecurrentExpenseEvents();
        List<Expense> expenses = expenseDao.findByUsername(expense.getUser().getUsername());
        assertEquals(1, expenses.size());
    }

    @Test
    public void shouldNotAddRecurrentIncomeOn2MonthFrequency(){
        Income income = createIncomeWith2MonthFrequency();
        income.setStartMonth(income.getStartMonth() + 1);
        assertTrue(income.getId() > 0);
        recurrentEventService.addRecurrentIncomeEvents();
        List<Income> incomes = incomeDao.findByUsername(income.getUser().getUsername());
        assertEquals(1, incomes.size());
    }

    private Expense createExpenseWithQuarterlyFrequency(){
        Expense expense = createBasicExpense();
        expense.setFrequency(String.valueOf(QUARTERLY));
        expense.setStartDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        expense.setStartMonth(Calendar.getInstance().get(Calendar.MONTH)+1);
        expenseDao.save(expense);
        return expense;
    }

    private Expense createExpenseWithMonthlyFrequency() {
        Expense expense = createBasicExpense();
        expense.setFrequency("*");
        expense.setStartDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        expense.setStartMonth(Calendar.getInstance().get(Calendar.MONTH)+1);
        expenseDao.save(expense);
        return expense;
    }

    private Expense createExpenseWith2MonthFrequency(){
        Expense expense = createBasicExpense();
        expense.setFrequency(String.valueOf(TWO_MONTH));
        expense.setStartDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        expense.setStartMonth(Calendar.getInstance().get(Calendar.MONTH)+1);
        expenseDao.save(expense);
        return expense;
    }

    private Income createIncomeWithMonthlyFrequency() {
        Income income = createBasicIncome();
        income.setFrequency("*");
        income.setStartDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        income.setStartMonth(Calendar.getInstance().get(Calendar.MONTH)+1);
        incomeDao.save(income);
        return income;
    }

    private Income createIncomeWith2MonthFrequency(){
        Income income = createBasicIncome();
        income.setFrequency(String.valueOf(TWO_MONTH));
        income.setStartDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        income.setStartMonth(Calendar.getInstance().get(Calendar.MONTH)+1);
        incomeDao.save(income);
        return income;
    }

    private Income createBasicIncome() {
        Income income = new Income();
        income.setName("name1");
        income.setDescription("description1");
        income.setAmount(new Double(222.222));
        income.setCurrency("USD");
        income.setCreationDate(new Timestamp(System.currentTimeMillis()));
        income.setUser(applicationUser);
        return income;
    }

    private Expense createBasicExpense() {
        Expense expense = new Expense();
        expense.setName("name1");
        expense.setCurrency("USD");
        expense.setDescription("description1");
        expense.setAmount(new Double(222.222));
        expense.setCreationDate(new Timestamp(System.currentTimeMillis()));
        expense.setUser(applicationUser);
        expense.setCategory(category);
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
