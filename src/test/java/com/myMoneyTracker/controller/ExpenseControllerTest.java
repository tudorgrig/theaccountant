package com.myMoneyTracker.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.myMoneyTracker.controller.exception.BadRequestException;
import com.myMoneyTracker.controller.exception.NotFoundException;
import com.myMoneyTracker.dao.AppUserDao;
import com.myMoneyTracker.dao.CategoryDao;
import com.myMoneyTracker.dao.ExpenseDao;
import com.myMoneyTracker.dto.expense.ExpenseDTO;
import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.ControllerUtil;

/**
 * Test class for the expense controller
 *
 * @author Florin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-config.xml"})
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

        categoryDao.delete(category.getId());
        categoryDao.flush();

        appUserDao.delete(applicationUser.getId());
        appUserDao.flush();

    }

    @Test
    public void shouldCreateExpense() {

        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((ExpenseDTO) responseEntity.getBody()).getId() > 0);
        expenseDao.delete(expense.getId());
        expenseDao.flush();
    }

    @Test
    public void shouldCreateRecurrentExpense() {

        Expense expense = createExpense(category, applicationUser);
        expense.setFrequency(1);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((ExpenseDTO) responseEntity.getBody()).getId() > 0);
        expenseDao.delete(expense.getId());
        expenseDao.flush();
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotCreateExpenseWithBadCurrency() {

        Expense expense = createExpense(category, applicationUser);
        expense.setCurrency("Pikachu");
        expenseController.createExpense(expense);
    }

    @Test
    public void shouldCreateExpenseWithANewCategory() {

        Category category = new Category();
        category.setName("new_created_category");
        category.setUser(applicationUser);
        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((ExpenseDTO) responseEntity.getBody()).getId() > 0);
        expenseDao.delete(expense.getId());
        expenseDao.flush();
        categoryDao.delete(expense.getCategory().getId());
        categoryDao.flush();
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotCreateExpense() {
        Expense expense = createExpense(category, applicationUser);
        expense.setName(null);
        expenseController.createExpense(expense);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldListAllExpense() {

        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        responseEntity = expenseController.listAllExpenses();
        assertEquals(1, ((List<ExpenseDTO>) responseEntity.getBody()).size());
        ExpenseDTO result = ((List<ExpenseDTO>) responseEntity.getBody()).get(0);
        assertEquals(expense.getName(), result.getName());
        expenseDao.delete(expense.getId());
        expenseDao.flush();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldListAllExpenseByCategoryName() {

        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        responseEntity = expenseController.listAllExpensesByCategoryName(category.getName());
        assertEquals(1, ((List<ExpenseDTO>) responseEntity.getBody()).size());
        ExpenseDTO result = ((List<ExpenseDTO>) responseEntity.getBody()).get(0);
        assertEquals(expense.getName(), result.getName());
        expenseDao.delete(expense.getId());
        expenseDao.flush();
    }

    @Test
    public void shouldListAllExpenseByCategoryNameAndTimeInterval() {

        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        long queryStartTime = expense.getCreationDate().getTime() - 1000;
        long queryEndTime = expense.getCreationDate().getTime() + 1000;

        responseEntity = expenseController.listAllExpensesByCategoryNameAndTimeInterval(category.getName(), "USD",
                queryStartTime, queryEndTime);
        assertEquals(1, ((List<ExpenseDTO>) responseEntity.getBody()).size());
        ExpenseDTO result = ((List<ExpenseDTO>) responseEntity.getBody()).get(0);
        assertEquals(expense.getName(), result.getName());

        responseEntity = expenseController.listAllExpensesByCategoryNameAndTimeInterval("*", "USD",
                queryStartTime, queryEndTime);
        assertEquals(1, ((List<ExpenseDTO>) responseEntity.getBody()).size());
        result = ((List<ExpenseDTO>) responseEntity.getBody()).get(0);
        assertEquals(expense.getName(), result.getName());

        expenseDao.delete(expense.getId());
        expenseDao.flush();
    }

    @Test
    public void shouldNotListAllExpenseByCategoryNameAndTimeIntervalForInvalidInterval() {

        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        long queryStartTime = expense.getCreationDate().getTime() + 1000;
        long queryEndTime = expense.getCreationDate().getTime() + 2000;

        responseEntity = expenseController.listAllExpensesByCategoryNameAndTimeInterval("*", "USD",
                queryStartTime, queryEndTime);
        assertTrue(((List<ExpenseDTO>) responseEntity.getBody()) == null
                || ((List<ExpenseDTO>) responseEntity.getBody()).size() == 0);

        expenseDao.delete(expense.getId());
        expenseDao.flush();
    }

    @Test
    public void shouldNotListAllExpenseByCategoryNameAndTimeIntervalForInvalidCategory() {

        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        long queryStartTime = expense.getCreationDate().getTime() - 1000;
        long queryEndTime = expense.getCreationDate().getTime() + 1000;

        responseEntity = expenseController.listAllExpensesByCategoryNameAndTimeInterval("Another category", "USD",
                queryStartTime, queryEndTime);
        assertTrue(((List<ExpenseDTO>) responseEntity.getBody()) == null
                || ((List<ExpenseDTO>) responseEntity.getBody()).size() == 0);

        expenseDao.delete(expense.getId());
        expenseDao.flush();
    }

    @Test
    public void shouldFindById() {

        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        responseEntity = expenseController.findExpense(((ExpenseDTO) responseEntity.getBody()).getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ExpenseDTO found = (ExpenseDTO) responseEntity.getBody();
        assertEquals(expense.getName(), found.getName());
        expenseDao.delete(expense.getId());
        expenseDao.flush();
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotFindById() {

        expenseController.findExpense(new Random().nextLong());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateExpense() {

        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        long id = expense.getId();
        Expense toUpdate = createExpense(category, applicationUser);
        toUpdate.setName("updated_expense");
        responseEntity = expenseController.updateExpense(expense.getId(), toUpdate);
        assertEquals("Should update expense with an existent category!", HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        Category category = new Category();
        category.setName("another_category");
        toUpdate.setCategory(category);
        responseEntity = expenseController.updateExpense(expense.getId(), toUpdate);
        assertEquals("Should update expense with a non-existent category!", HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        responseEntity = expenseController.listAllExpenses();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ExpenseDTO> found = (List<ExpenseDTO>) responseEntity.getBody();
        assertEquals("updated_expense", found.get(0).getName());

        expenseDao.delete(id);
        expenseDao.flush();
        categoryDao.delete(found.get(0).getCategory().getId());
        categoryDao.flush();
    }

    @Test
    public void shouldNotUpdateExpenseWithWrongCurrency() {

        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
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
            expenseDao.delete(expense.getId());
            expenseDao.flush();
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
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
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
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        responseEntity = expenseController.deleteAll();
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        responseEntity = expenseController.listAllExpenses();
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void shouldDeleteAllExpensesByCategoryName() {

        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        responseEntity = expenseController.deleteAllByCategoryName(category.getName());
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        responseEntity = expenseController.listAllExpensesByCategoryName(category.getName());
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    private Expense createExpense(Category category, AppUser user) {

        Expense expense = new Expense();
        expense.setName("name1");
        expense.setCategory(category);
        expense.setUser(user);
        expense.setCurrency("USD");
        expense.setDescription("description1");
        expense.setAmount(new Double(222.222));
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
        appUser = appUserDao.saveAndFlush(appUser);
        return appUser;
    }
}
