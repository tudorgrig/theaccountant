package com.myMoneyTracker.dao;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

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
	
	private static final Logger logger = Logger.getLogger(IncomeDaoTest.class.getName());

	
	@Test
    public void shouldSaveIncome() {
		Income income = createIncome();
		income = incomeDao.save(income);
		logger.info("The income has id = " + income.getId());
        assertTrue(income.getId() != 0);
	}
	
	@Test
    public void shouldFindIncome() {
		Income income = createIncome();
		income = incomeDao.save(income);
		income = incomeDao.findOne(income.getId());
		assertTrue(income != null);
	}
	
	@Test
    public void shouldNotFindIncome() {
		Income income = createIncome();
		income = incomeDao.findOne(new Random().nextLong());
		assertTrue(income == null);
	}
	
	@Test
    public void shouldUpdateIncome() {
		String updatedName = "NameUpdated";
		Income income = createIncome();
		income = incomeDao.save(income);
		income.setName(updatedName);
		Income result = incomeDao.save(income);
		assertTrue(result.getName().equals(updatedName));
	}
	
	@Test
    public void shouldSaveAndFlush() {
		Income income = createIncome();
		income = incomeDao.saveAndFlush(income);
		assertTrue(income.getId() > 0);
	}
	
	@Test
    public void shouldFindAll(){
		Income income1 = createIncome();
		Income income2 = createIncome();
		//2 different incomes will be saved into the databes
		// because the id for both is null
		incomeDao.save(income1);
		incomeDao.save(income2);
		List<Income> incomeList = incomeDao.findAll();
		assertEquals(2, incomeList.size());
	}
	
	@Test
	public void shouldHaveCategoryNotNull() {
		Income income = createIncome();
		income = incomeDao.save(income);
		assertTrue(income.getCategory() != null);
	}
	
	@Test
	public void shouldHaveUserNotNull() {
		Income income = createIncome();
		income = incomeDao.save(income);
		assertTrue(income.getUser() != null);
	}
	
	private Income createIncome() {
		Income income = new Income();
		income.setName("name1");
		income.setDescription("description1");
		income.setAmount(new Double(222.222));
		income.setCreationDate(new Timestamp(System.currentTimeMillis()));
		income.setUser(createAppUser());
		income.setCategory(createCategory());
		return income;
	}
	
    private AppUser createAppUser() {
    	AppUser appUser = new AppUser();
    	appUser.setFirstName("Florin");
    	appUser.setSurname("Iacob");
		appUser.setPassword("TEST_PASS");
    	appUser.setBirthdate(new Date());
    	appUserDao.save(appUser);
    	return appUser;
    }
    
	private Category createCategory() {
		Category category = new Category();
		category.setName("Florin");
		categoryDao.save(category);
		return category;
	}
}
