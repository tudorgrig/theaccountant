package com.myMoneyTracker.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import com.myMoneyTracker.model.user.AppUser;

/**
 * This class represents the test class for the 'category' data access object
 * 
 * @author Florin Iacob
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/spring-config.xml"})
@Transactional
public class CategoryDaoTest {
	
	private static final Logger logger = Logger.getLogger(CategoryDaoTest.class.getName());

	private static final String USERNAME = "Username1";
	private static final String CATEGORY_NAME = "Category1";

	@Autowired
	private CategoryDao categoryDao;
	
	@Autowired
	private AppUserDao appUserDao;
	
	private AppUser applicationUser = null;
	
	@Before
	public void initialize() {
		applicationUser = createAppUser();
	}
	
	@Test
	public void shouldSaveCategory() {
		Category category = createCategory();
		category = categoryDao.save(category);
		logger.info("The category has id = " + category.getId());
		assertTrue(category.getId() != 0);
	}
	
	@Test
	public void shouldFindCategory() {
		Category category = createCategory();
		category = categoryDao.save(category);
		category = categoryDao.findOne(category.getId());
		assertTrue(category != null);
	}
	
	@Test
	public void shouldFindCategoryByNameAndUsername() {
		Category category = createCategory();
		category = categoryDao.save(category);
		category = categoryDao.findCategoryByNameAndUsername(CATEGORY_NAME, USERNAME);
		assertTrue(category != null);
	}
	
	@Test
	public void shouldFindCategoriesByUsername() {
		Category category1 = createCategory();
		Category category2 = createCategory();
		//2 different expenses will be saved into the database
		// because the id for both is null
		categoryDao.save(category1);
		categoryDao.save(category2);
		List<Category> categoryList = categoryDao.findCategoriesByUsername(USERNAME);
		assertEquals(2, categoryList.size());
	}
	
	@Test
    public void shouldFindAll(){
		Category category1 = createCategory();
		Category category2 = createCategory();
		//2 different expenses will be saved into the database
		// because the id for both is null
		categoryDao.save(category1);
		categoryDao.save(category2);
		List<Category> categoryList = categoryDao.findAll();
		assertEquals(2, categoryList.size());
	}
	
	@Test
	public void shouldNotFindCategory() {
		Category category = createCategory();
		category = categoryDao.findOne(new Random().nextLong());
		assertTrue(category == null);
	}
	
	@Test
	public void shouldDeleteCategory() {
		Category category = createCategory();
		category = categoryDao.save(category);
		categoryDao.delete(category);
		category = categoryDao.findOne(category.getId());
		assertTrue(category == null);
	}
	
	@Test
	public void shouldUpdateCategory() {
		Category category = createCategory();
		category = categoryDao.save(category);
		category.setName("Tudor");
		Category result = categoryDao.save(category);
		assertTrue(result.getName().equals("Tudor"));
	}
	
	@Test
	public void shouldSaveAndFlush() {
		Category category = createCategory();
		category = categoryDao.saveAndFlush(category);
		assertTrue(category.getId() > 0);
	}
	
	private Category createCategory() {
		Category category = new Category();
		category.setName(CATEGORY_NAME);
		category.setUser(applicationUser);
		return category;
	}
	
    private AppUser createAppUser() {
    	AppUser appUser = new AppUser();
    	appUser.setFirstName("Florin");
    	appUser.setSurname("Iacob");
    	appUser.setBirthdate(new Date());
    	appUser.setUsername(USERNAME);
    	appUserDao.save(appUser);
    	return appUser;
    }
}
