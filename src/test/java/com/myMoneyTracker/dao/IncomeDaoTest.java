package com.myMoneyTracker.dao;

import static org.junit.Assert.*;

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
import com.myMoneyTracker.model.income.Income;
import com.myMoneyTracker.model.user.AppUser;

/**
 *  This class represents the test class for the 'income' data access object
 * 
 * @author Florin, on 19.12.2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/spring-config.xml"})
@Transactional
public class IncomeDaoTest {

	@Autowired
	private IncomeDao incomeDao;
	
	@Autowired
	private AppUserDao appUserDao;
	
	@Autowired
	private CategoryDao categoryDao;

	private static final String USERNAME = "tudorgrig";
	private static final String EMAIL = "help.mmt@gmail.com";
	
	private static final Logger logger = Logger.getLogger(IncomeDaoTest.class.getName());

	@Before
	public void cleanUp(){
		appUserDao.deleteAll();
		appUserDao.flush();
		incomeDao.deleteAll();
		incomeDao.flush();
	}

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
    public void shouldNotFindIncome() {
		assertTrue(incomeDao.findOne(new Random().nextLong()) == null);
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
    public void shouldFindAll(){
		AppUser appUser = createAppUser(EMAIL, USERNAME);
		Income income1 = createIncome(appUser);
		AppUser appUser2 = createAppUser("test@test.com", "test_username");
		Income income2 = createIncome(appUser2);
		incomeDao.save(income1);
		incomeDao.save(income2);
		List<Income> incomeList = incomeDao.findAll();
		assertEquals(2, incomeList.size());
	}
	
	@Test
	public void shouldHaveCategoryNotNull() {
		AppUser appUser = createAppUser(EMAIL, USERNAME);
		Income income = createIncome(appUser);
		income = incomeDao.save(income);
		assertTrue(income.getCategory() != null);
	}
	
	@Test
	public void shouldHaveUserNotNull() {
		AppUser appUser = createAppUser(EMAIL, USERNAME);
		Income income = createIncome(appUser);
		income = incomeDao.save(income);
		assertTrue(income.getUser() != null);
	}
	
	private Income createIncome(AppUser appUser) {
		Income income = new Income();
		income.setName("name1");
		income.setDescription("description1");
		income.setAmount(new Double(222.222));
		income.setCreationDate(new Timestamp(System.currentTimeMillis()));
		income.setUser(appUser);
		income.setCategory(createCategory(appUser));
		return income;
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
