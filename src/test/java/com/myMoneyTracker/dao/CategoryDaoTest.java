package com.myMoneyTracker.dao;

import java.util.Random;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.myMoneyTracker.model.category.Category;

import static org.junit.Assert.assertTrue;

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

	@Autowired
	private CategoryDao categoryDao;
	
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
		category.setName("Florin");
		return category;
	}
	
}
