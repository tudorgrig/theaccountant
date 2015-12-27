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

import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.user.AppUser;

/**
 *  This class represents the test class for the 'expense' data access object
 * 
 * @author Florin, on 20.12.2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/spring-config.xml"})
@Transactional
public class ExpenseDaoTest {

	@Autowired
	private ExpenseDao expenseDao;
	
	@Autowired
	private AppUserDao appUserDao;
	
	@Autowired
	private CategoryDao categoryDao;
	
	private static final Logger logger = Logger.getLogger(ExpenseDaoTest.class.getName());
	
	private AppUser applicationUser = null;

	@Before
	public void initialize() {
		applicationUser = createAppUser("test@my-money-tracker.ro", "user1");
	}
	
	@Test
    public void shouldSaveExpense() {
		Expense expense = createExpense();
		expense = expenseDao.save(expense);
		logger.info("The expense has id = " + expense.getId());
        assertTrue(expense.getId() != 0);
	}
	
	@Test
    public void shouldFindExpense() {
		Expense expense = createExpense();
		expense = expenseDao.save(expense);
		expense = expenseDao.findOne(expense.getId());
		assertTrue(expense != null);
	}
	
	@Test
    public void shouldNotFindExpense() {
		Expense expense = createExpense();
		expense = expenseDao.findOne(new Random().nextLong());
		assertTrue(expense == null);
	}
	
	@Test
    public void shouldUpdateExpense() {
		String updatedName = "NameUpdated";
		Expense expense = createExpense();
		expense = expenseDao.save(expense);
		expense.setName(updatedName);
		Expense result = expenseDao.save(expense);
		assertTrue(result.getName().equals(updatedName));
	}
	
	@Test
    public void shouldSaveAndFlush() {
		Expense expense = createExpense();
		expense = expenseDao.saveAndFlush(expense);
		assertTrue(expense.getId() > 0);
	}
	
	@Test
    public void shouldFindAll(){
		Expense expense1 = createExpense();
		Expense expense2 = createExpense();
		//2 different expenses will be saved into the database
		// because the id for both is null
		expenseDao.save(expense1);
		expenseDao.save(expense2);
		List<Expense> expenseList = expenseDao.findAll();
		assertEquals(2, expenseList.size());
	}
	
	@Test
	public void shouldHaveCategoryNotNull() {
		Expense expense = createExpense();
		expense = expenseDao.save(expense);
		assertTrue(expense.getCategory() != null);
	}
	
	@Test
	public void shouldHaveUserNotNull() {
		Expense expense = createExpense();
		expense = expenseDao.save(expense);
		assertTrue(expense.getUser() != null);
	}
	
	private Expense createExpense() {
		Expense expense = new Expense();
		expense.setName("name1");
		expense.setDescription("description1");
		expense.setAmount(new Double(222.222));
		expense.setCreationDate(new Timestamp(System.currentTimeMillis()));
		expense.setUser(applicationUser);
		expense.setCategory(createCategory(applicationUser));
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
