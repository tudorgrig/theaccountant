package com.TheAccountant.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.controller.exception.NotFoundException;
import com.TheAccountant.dao.AppUserDao;
import com.TheAccountant.dao.CategoryDao;
import com.TheAccountant.dao.ExpenseDao;
import com.TheAccountant.dto.expense.ExpenseDTO;
import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.expense.Expense;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.util.ControllerUtil;

/**
 * Test class for the expense controller
 *
 * @author Florin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-config.xml"})
@TestPropertySource(locations="classpath:application-test.properties")
public class ExpenseControllerTest {

    private static final String LOGGED_USERNAME = "florin.iacob";
    private static final String CATEGORY_NAME = "mmtcategory_test_name";

    private AppUser applicationUser;
    private Category category;

    @Autowired
    private ExpenseController expenseController;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private ExpenseDao expenseDao;

    @Before
    public void setup() {

        applicationUser = createAndSaveAppUser(LOGGED_USERNAME, "florin.iacob.expense@gmail.com");
        ControllerUtil.setCurrentLoggedUser(LOGGED_USERNAME);
        category = createAndSaveCategory(applicationUser);
    }

    @After
    public void cleanUp() {
        appUserDao.delete(applicationUser.getUserId());
        appUserDao.flush();

    }

    @Test
    public void shouldCreateExpense() {

        Expense expense1 = createExpense(category, applicationUser);
        Expense expense2 = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[2];
        expenses[0] = expense1;
        expenses[1] = expense2;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((ExpenseDTO[]) responseEntity.getBody()).length == 2);
    }

    @Test
    public void shouldCreateExpenseWithDefaultCurrencyAndAmount() {

        Expense expense = createExpense(category, applicationUser);
        expense.setCurrency("EUR");
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((ExpenseDTO[]) responseEntity.getBody()).length == 1);
        assertNotNull(((ExpenseDTO[]) responseEntity.getBody())[0].getDefaultCurrency());
        assertNotNull(((ExpenseDTO[]) responseEntity.getBody())[0].getDefaultCurrencyAmount());
    }

    @Test
    public void shouldCreateRecurrentExpense() {

        Expense expense = createExpense(category, applicationUser);
        expense.setFrequency(1);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((ExpenseDTO[]) responseEntity.getBody()).length == 1);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotCreateExpenseWithBadCurrency() {

        Expense expense = createExpense(category, applicationUser);
        expense.setCurrency("Pikachu");
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        expenseController.createExpenses(expenses);
    }

    @Test
    public void shouldCreateExpenseWithANewCategory() {

        Category category = new Category();
        category.setName("new_created_category");
        category.setUser(applicationUser);
        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((ExpenseDTO[]) responseEntity.getBody()).length == 1);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotCreateExpense() {
        Expense expense = createExpense(category, applicationUser);
        expense.setName(null);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        expenseController.createExpenses(expenses);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldListAllExpense() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        responseEntity = expenseController.listAllExpenses();
        assertEquals(1, ((List<ExpenseDTO>) responseEntity.getBody()).size());
        ExpenseDTO result = ((List<ExpenseDTO>) responseEntity.getBody()).get(0);
        assertEquals(expense.getName(), result.getName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldListAllExpenseByCategoryName() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        responseEntity = expenseController.listAllExpensesByCategoryName(category.getName());
        assertEquals(1, ((List<ExpenseDTO>) responseEntity.getBody()).size());
        ExpenseDTO result = ((List<ExpenseDTO>) responseEntity.getBody()).get(0);
        assertEquals(expense.getName(), result.getName());
    }

    @Test
    public void shouldListAllExpenseByCategoryNameAndTimeInterval() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        long queryStartTime = expense.getCreationDate().getTime() - 1000;
        long queryEndTime = expense.getCreationDate().getTime() + 1000;

        responseEntity = expenseController.listAllExpensesByCategoryAndTimeInterval(String.valueOf(category.getId()),
                queryStartTime, queryEndTime);
        assertEquals(1, ((List<ExpenseDTO>) responseEntity.getBody()).size());
        ExpenseDTO result = ((List<ExpenseDTO>) responseEntity.getBody()).get(0);
        assertEquals(expense.getName(), result.getName());
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowExceptionIdIsNotNumber() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        long queryStartTime = expense.getCreationDate().getTime() - 1000;
        long queryEndTime = expense.getCreationDate().getTime() + 1000;

        expenseController.listAllExpensesByCategoryAndTimeInterval("BAD_ID",
                queryStartTime, queryEndTime);
    }

    @Test
    public void shouldListAllExpenseForAllCategoriesAndTimeInterval() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        long queryStartTime = expense.getCreationDate().getTime() - 1000;
        long queryEndTime = expense.getCreationDate().getTime() + 1000;

        responseEntity = expenseController.listAllExpensesByCategoryAndTimeInterval("*",
                queryStartTime, queryEndTime);
        assertEquals(1, ((List<ExpenseDTO>) responseEntity.getBody()).size());
        ExpenseDTO result = ((List<ExpenseDTO>) responseEntity.getBody()).get(0);
        assertEquals(expense.getName(), result.getName());
    }

    @Test
    public void shouldListAllExpensesForAllCategAndTimeAndSetDefCurrAmount() {

        Expense expense = createExpense(category, applicationUser);
        expense.setCurrency("EUR");
        expense = expenseDao.save(expense);
        long queryStartTime = expense.getCreationDate().getTime() - 1000;
        long queryEndTime = expense.getCreationDate().getTime() + 1000;

        ResponseEntity responseEntity = expenseController.listAllExpensesByCategoryAndTimeInterval("*",
                queryStartTime, queryEndTime);
        assertEquals(1, ((List<ExpenseDTO>) responseEntity.getBody()).size());
        ExpenseDTO result = ((List<ExpenseDTO>) responseEntity.getBody()).get(0);
        assertEquals(expense.getName(), result.getName());
        assertEquals(applicationUser.getDefaultCurrency().getCurrencyCode(), result.getDefaultCurrency());
    }


    @Test
    public void shouldNotListAllExpenseByCategoryNameAndTimeIntervalForInvalidInterval() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        long queryStartTime = expense.getCreationDate().getTime() + 1000;
        long queryEndTime = expense.getCreationDate().getTime() + 2000;

        responseEntity = expenseController.listAllExpensesByCategoryAndTimeInterval("*",
                queryStartTime, queryEndTime);
        assertTrue(((List<ExpenseDTO>) responseEntity.getBody()) == null
                || ((List<ExpenseDTO>) responseEntity.getBody()).size() == 0);
    }

    @Test
    public void shouldNotListAllExpenseByCategoryNameAndTimeIntervalForInvalidCategory() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        long queryStartTime = expense.getCreationDate().getTime() - 1000;
        long queryEndTime = expense.getCreationDate().getTime() + 1000;

        responseEntity = expenseController.listAllExpensesByCategoryAndTimeInterval(String.valueOf(777),
                queryStartTime, queryEndTime);
        assertTrue(((List<ExpenseDTO>) responseEntity.getBody()) == null
                || ((List<ExpenseDTO>) responseEntity.getBody()).size() == 0);
    }

    @Test
    public void shouldFindById() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        responseEntity = expenseController.findExpense(((ExpenseDTO[]) responseEntity.getBody())[0].getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ExpenseDTO found = (ExpenseDTO) responseEntity.getBody();
        assertEquals(expense.getName(), found.getName());
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotFindById() {

        expenseController.findExpense(new Random().nextLong());
    }

    @Test
    @SuppressWarnings("unchecked")

    public void shouldUpdateExpense() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Expense toUpdate = createExpense(category, applicationUser);
        toUpdate.setName("updated_expense");
        responseEntity = expenseController.updateExpense(expense.getId(), toUpdate);
        assertEquals("Should update expense with an existent category!", HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        Category category = new Category();
        category.setName("another_category");
        category.setUser(applicationUser);
        toUpdate.setCategory(category);
        responseEntity = expenseController.updateExpense(expense.getId(), toUpdate);
        assertEquals("Should update expense with a non-existent category!", HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        responseEntity = expenseController.findExpense(expense.getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ExpenseDTO found = (ExpenseDTO) responseEntity.getBody();
        assertEquals("updated_expense", found.getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateExpenseAndSetDefaultCurrencyAmount() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Expense toUpdate = createExpense(category, applicationUser);
        toUpdate.setName("updated_expense");
        toUpdate.setCurrency("EUR");
        responseEntity = expenseController.updateExpense(expense.getId(), toUpdate);
        assertEquals("Should update expense with an existent category!", HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        Category category = new Category();
        category.setName("another_category");
        toUpdate.setCategory(category);
        responseEntity = expenseController.updateExpense(expense.getId(), toUpdate);
        assertEquals("Should update expense with a non-existent category!", HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        responseEntity = expenseController.findExpense(expense.getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ExpenseDTO found = (ExpenseDTO) responseEntity.getBody();
        assertEquals("updated_expense", found.getName());
    }


    @Test
    public void shouldNotUpdateExpenseWithWrongCurrency() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Expense toUpdate = createExpense(category, applicationUser);
        toUpdate.setName("updated_expense");
        responseEntity = expenseController.updateExpense(expense.getId(), toUpdate);
        assertEquals("Should update expense with an existent category!", HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        Category category = new Category();
        category.setName("another_category");
        toUpdate.setCategory(category);
        toUpdate.setCurrency("IAC");
        try {
            expenseController.updateExpense(expense.getId(), toUpdate);
        } catch(Exception e){
            assertTrue(e instanceof BadRequestException);
        }
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotUpdateExpense() {

        Expense expense = createExpense(category, applicationUser);
        @SuppressWarnings("unused")
        ResponseEntity<?> responseEntity = expenseController.updateExpense(111L, expense);
    }

    @Test(expected = NotFoundException.class)
    public void shouldDeleteExpense() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        responseEntity = expenseController.deleteExpense(expense.getId());
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        expenseController.findExpense(expense.getId());
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotDeleteExpense() {

        expenseController.deleteExpense(-1L);
    }

    @Test
    public void shouldDeleteAllExpenses() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        responseEntity = expenseController.deleteAll();
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        responseEntity = expenseController.listAllExpenses();
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void shouldDeleteAllExpensesByCategoryName() {

        Expense expense = createExpense(category, applicationUser);
        Expense[] expenses = new Expense[1];
        expenses[0] = expense;

        ResponseEntity<?> responseEntity = expenseController.createExpenses(expenses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        responseEntity = expenseController.deleteAllByCategory(category.getId());
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        responseEntity = expenseController.listAllExpensesByCategoryName(category.getName());
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    private Expense createExpense(Category category, AppUser user) {

        Expense expense = new Expense();
        expense.setName("name1");
        expense.setCategory(category);
        expense.setUser(user);
        expense.setCurrency("RON");
        expense.setDescription("description1");
        expense.setAmount(new Double(10));
        expense.setCreationDate(new Timestamp(System.currentTimeMillis()));
        return expense;
    }

    private Category createAndSaveCategory(AppUser user) {

        Category category = new Category();
        category.setName(CATEGORY_NAME);
        category.setUser(user);
        categoryDao.saveAndFlush(category);
        return category;
    }

    private AppUser createAndSaveAppUser(String username, String email) {

        AppUser appUser = new AppUser();
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setBirthdate(new Date());
        appUser.setUsername(username);
        appUser.setEmail(email);
        appUser.setDefaultCurrency(Currency.getInstance("RON"));
        appUser = appUserDao.saveAndFlush(appUser);
        return appUser;
    }
}
