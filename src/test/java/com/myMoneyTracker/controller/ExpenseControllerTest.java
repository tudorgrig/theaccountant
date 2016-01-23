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

import com.myMoneyTracker.dao.AppUserDao;
import com.myMoneyTracker.dao.CategoryDao;
import com.myMoneyTracker.dao.UserRegistrationDao;
import com.myMoneyTracker.dto.expense.ExpenseDTO;
import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.ControllerUtil;

/**
 * est class for the expense controller
 * 
 * @author Florin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
public class ExpenseControllerTest {
    
    private static final String LOGGED_USERNAME = "florin.iacob";
    
    private AppUser applicationUser;
    
    @Autowired
    private ExpenseController expenseController;
    
    @Autowired
    private CategoryDao categoryDao;
    
    @Autowired
    private AppUserDao appUserDao;
    
    @Autowired
    private UserRegistrationDao userRegistrationDao;
    
    @Before
    public void setup() {
    
        applicationUser = createAndSaveAppUser(LOGGED_USERNAME);
        ControllerUtil.setCurrentLoggedUser(LOGGED_USERNAME);
    }
    
    @After
    public void cleanUp() {
    
        userRegistrationDao.deleteAll();
        userRegistrationDao.flush();
        expenseController.deleteAll();
        categoryDao.deleteAll();
        categoryDao.flush();
        appUserDao.deleteAll();
        appUserDao.flush();
    }
    
    @Test
    public void shouldCreateExpense() {
    
        Category category = createAndSaveCategory(applicationUser);
        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((ExpenseDTO) responseEntity.getBody()).getId() > 0);
    }
    
    @Test
    public void shouldNotCreateExpense() {
    
        Category category = createAndSaveCategory(applicationUser);
        Expense expense = createExpense(category, applicationUser);;
        expense.setName(null);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldListAllExpense() {
    
        Category category = createAndSaveCategory(applicationUser);
        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        responseEntity = expenseController.listAllExpenses();
        assertEquals(1, ((List<ExpenseDTO>) responseEntity.getBody()).size());
        ExpenseDTO result = ((List<ExpenseDTO>) responseEntity.getBody()).get(0);
        assertEquals(expense.getName(), result.getName());
    }
    
    @Test
    public void shouldFindById() {
    
        Category category = createAndSaveCategory(applicationUser);
        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        responseEntity = expenseController.findExpense(((ExpenseDTO) responseEntity.getBody()).getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ExpenseDTO found = (ExpenseDTO) responseEntity.getBody();
        assertEquals(expense.getName(), found.getName());
    }
    
    @Test
    public void shouldNotFindById() {
    
        ResponseEntity<?> responseEntity = expenseController.findExpense(new Random().nextLong());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
    
    @Test
    public void shouldUpdateExpense() {
    
        Category category = createAndSaveCategory(applicationUser);
        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Expense toUpdate = createExpense(category, applicationUser);
        toUpdate.setName("updated_expense");
        responseEntity = expenseController.updateExpense(expense.getId(), toUpdate);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        
        responseEntity = expenseController.listAllExpenses();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        @SuppressWarnings("unchecked")
        List<ExpenseDTO> found = (List<ExpenseDTO>) responseEntity.getBody();
        assertEquals("updated_expense", found.get(0).getName());
    }
    
    @Test
    public void shouldDeleteExpense() {
    
        Category category = createAndSaveCategory(applicationUser);
        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        responseEntity = expenseController.deleteExpense(expense.getId());
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        
        responseEntity = expenseController.findExpense(expense.getId());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
    
    @Test
    public void shouldDeleteAllExpenses() {
    
        Category category = createAndSaveCategory(applicationUser);
        Expense expense = createExpense(category, applicationUser);
        ResponseEntity<?> responseEntity = expenseController.createExpense(expense);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        responseEntity = expenseController.deleteAll();
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        
        responseEntity = expenseController.listAllExpenses();
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }
    
    private Expense createExpense(Category category, AppUser user) {
    
        Expense expense = new Expense();
        expense.setName("name1");
        expense.setCategory(category);
        expense.setUser(user);
        expense.setDescription("description1");
        expense.setAmount(new Double(222.222));
        expense.setCreationDate(new Timestamp(System.currentTimeMillis()));
        return expense;
    }
    
    private Category createAndSaveCategory(AppUser user) {
    
        Category category = new Category();
        category.setName("category1");
        category.setUser(user);
        categoryDao.saveAndFlush(category);
        return category;
    }
    
    private AppUser createAndSaveAppUser(String username) {
    
        AppUser appUser = new AppUser();
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setBirthdate(new Date());
        appUser.setUsername(LOGGED_USERNAME);
        appUser.setEmail("florin.iacob@gmail.com");
        appUser = appUserDao.saveAndFlush(appUser);
        return appUser;
    }
}
