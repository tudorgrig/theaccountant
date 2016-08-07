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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    public void shouldAddRecurrentExpenseOnExactDayAndConvertCurrency(){
        Expense expense = createExpenseWithMonthlyFrequency();
        expense.setCurrency("RON");
        expenseDao.save(expense);
        assertTrue(expense.getId() > 0);
        recurrentEventService.addRecurrentExpenseEvents();
        List<Expense> expenses = expenseDao.findByUsername(expense.getUser().getUsername());
        assertEquals(2, expenses.size());
    }

    @Test
    public void shouldAddRecurrentExpenseOnExactDayLastMonth() throws ParseException {
        Expense expense = createExpenseWithMonthlyFrequency();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String currentDayString = null;
        if(currentDay < 10){
            currentDayString = "0" + currentDay;
        }else{
            currentDayString = String.valueOf(currentDay);
        }
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int previousMonth = currentMonth - 1;
        if(previousMonth == 0){
            previousMonth = 12;
        }
        String currentMonthString = null;
        if(currentMonth < 10){
            currentMonthString = "0" + previousMonth;
        }else {
            currentMonthString = String.valueOf(previousMonth);
        }
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        Date date = dateFormat.parse(currentDayString + "/" + currentMonthString + "/" + currentYear) ;
        long time = date.getTime();
        expense.setCreationDate(new Timestamp(time));
        expenseDao.save(expense);
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
    public void shouldAddRecurrentIncomeOnExactDayAndConvertCurrency(){
        Income income = createIncomeWithMonthlyFrequency();
        income.setCurrency("RON");
        assertTrue(income.getId() > 0);
        recurrentEventService.addRecurrentIncomeEvents();
        List<Income> incomes = incomeDao.findByUsername(income.getUser().getUsername());
        assertEquals(2, incomes.size());
        assertNotNull(incomes.get(1).getDefaultCurrency());
        assertNotNull(incomes.get(1).getDefaultCurrencyAmount());
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
    public void shouldNotAddRecurrentExpenseOnQuarterlyFrequency() throws ParseException {
        Expense expense = createExpenseWithQuarterlyFrequency();
        expenseDao.save(expense);
        assertTrue(expense.getId() > 0);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String currentDayString = null;
        if(currentDay < 10){
            currentDayString = "0" + currentDay;
        }else{
            currentDayString = String.valueOf(currentDay);
        }
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int previousMonth = currentMonth - 1;
        if(previousMonth == 0){
            previousMonth = 12;
        }
        String currentMonthString = null;
        if(currentMonth < 10){
            currentMonthString = "0" + previousMonth;
        }else {
            currentMonthString = String.valueOf(previousMonth);
        }
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        Date date = dateFormat.parse(currentDayString + "/" + currentMonthString + "/" + currentYear) ;
        long time = date.getTime();
        expense.setCreationDate(new Timestamp(time));
        expenseDao.save(expense);

        recurrentEventService.addRecurrentExpenseEvents();
        List<Expense> expenses = expenseDao.findByUsername(expense.getUser().getUsername());
        assertEquals(1, expenses.size());
    }

    @Test
    public void shouldNotAddRecurrentIncomeOn2MonthFrequency() throws ParseException {
        Income income = createIncomeWith2MonthFrequency();
        assertTrue(income.getId() > 0);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String currentDayString = null;
        if(currentDay < 10){
            currentDayString = "0" + currentDay;
        }else{
            currentDayString = String.valueOf(currentDay);
        }
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int previousMonth = currentMonth - 1;
        if(previousMonth == 0){
            previousMonth = 12;
        }
        String currentMonthString = null;
        if(currentMonth < 10){
            currentMonthString = "0" + previousMonth;
        }else {
            currentMonthString = String.valueOf(previousMonth);
        }
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        Date date = dateFormat.parse(currentDayString + "/" + currentMonthString + "/" + currentYear) ;
        long time = date.getTime();
        income.setCreationDate(new Timestamp(time));
        incomeDao.save(income);

        recurrentEventService.addRecurrentIncomeEvents();
        List<Income> incomes = incomeDao.findByUsername(income.getUser().getUsername());
        assertEquals(1, incomes.size());
    }

    private Expense createExpenseWithQuarterlyFrequency(){
        Expense expense = createBasicExpense();
        expense.setFrequency(QUARTERLY);
        expenseDao.save(expense);
        return expense;
    }

    private Expense createExpenseWithMonthlyFrequency() {
        Expense expense = createBasicExpense();
        expense.setFrequency(1);
        expenseDao.save(expense);
        return expense;
    }

    private Expense createExpenseWith2MonthFrequency(){
        Expense expense = createBasicExpense();
        expense.setFrequency(TWO_MONTH);
        expenseDao.save(expense);
        return expense;
    }

    private Income createIncomeWithMonthlyFrequency() {
        Income income = createBasicIncome();
        income.setFrequency(1);
        incomeDao.save(income);
        return income;
    }

    private Income createIncomeWith2MonthFrequency(){
        Income income = createBasicIncome();
        income.setFrequency(TWO_MONTH);
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
        appUser.setDefaultCurrency(Currency.getInstance("USD"));
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
