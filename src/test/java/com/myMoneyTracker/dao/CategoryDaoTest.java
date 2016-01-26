package com.myMoneyTracker.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import com.myMoneyTracker.dao.CategoryDao;

import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.user.AppUser;

/**
 * This class represents the test class for the 'category' data access object
 *
 * @author Florin Iacob
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
public class CategoryDaoTest {

    private static final Logger logger = Logger.getLogger(CategoryDaoTest.class.getName());

    private static final String USERNAME = "Username";
    private static final String CATEGORY_NAME = "Category";

    private static int categoryCounter = 0;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private AppUserDao appUserDao;

    private AppUser applicationUser = null;

    @Before
    public void initialize() {

        categoryDao.deleteAll();
        applicationUser = createAppUser();
    }

    @Test
    public void shouldSaveCategory() {

        Category category = createCategory(CATEGORY_NAME + categoryCounter++);
        appUserDao.save(category.getUser());
        category = categoryDao.save(category);
        logger.info("The category has id = " + category.getId());
        assertTrue(category.getId() != 0);
    }

    @Test
    public void shouldFindCategory() {

        Category category = createCategory(CATEGORY_NAME + categoryCounter++);
        appUserDao.save(category.getUser());
        category = categoryDao.save(category);
        category = categoryDao.findOne(category.getId());
        assertTrue(category != null);
    }

    @Test
    public void shouldFindCategoryByNameAndUsername() {

        String categoryName = CATEGORY_NAME + categoryCounter++;
        Category category = createCategory(categoryName);
        appUserDao.save(category.getUser());
        categoryDao.save(category);
        category = categoryDao.findByNameAndUsername(categoryName, USERNAME);
        assertTrue(category != null);
    }

    @Test
    public void shouldFindCategoriesByUsername() {

        Category category1 = createCategory(CATEGORY_NAME + categoryCounter++);
        appUserDao.save(category1.getUser());
        Category category2 = createCategory(CATEGORY_NAME + categoryCounter++);
        appUserDao.save(category2.getUser());
        categoryDao.save(category1);
        categoryDao.save(category2);
        List<Category> categoryList = categoryDao.findByUsername(USERNAME);
        assertEquals(2, categoryList.size());
    }

    @Test
    public void shouldFindAll() {

        Category category1 = createCategory(CATEGORY_NAME + categoryCounter++);
        appUserDao.save(category1.getUser());
        Category category2 = createCategory(CATEGORY_NAME + categoryCounter++);
        appUserDao.save(category2.getUser());
        categoryDao.save(category1);
        categoryDao.save(category2);
        List<Category> categoryList = categoryDao.findAll();
        assertEquals(2, categoryList.size());
    }

    @Test
    public void shouldNotFindCategory() {

        Category category = categoryDao.findOne(new Random().nextLong());
        assertTrue(category == null);
    }

    @Test
    public void shouldDeleteCategory() {

        Category category = createCategory(CATEGORY_NAME + categoryCounter++);
        appUserDao.save(category.getUser());
        category = categoryDao.save(category);
        categoryDao.delete(category);
        category = categoryDao.findOne(category.getId());
        assertTrue(category == null);
    }

    @Test
    public void shouldUpdateCategory() {

        Category category = createCategory(CATEGORY_NAME + categoryCounter++);
        appUserDao.save(category.getUser());
        category = categoryDao.save(category);
        category.setName("Tudor");
        Category result = categoryDao.save(category);
        assertTrue(result.getName().equals("Tudor"));
    }

    @Test
    public void shouldSaveAndFlush() {

        Category category = createCategory(CATEGORY_NAME + categoryCounter++);
        appUserDao.save(category.getUser());
        category = categoryDao.saveAndFlush(category);
        assertTrue(category.getId() > 0);
    }

    @Test
    public void shouldDeleteAllCategoriesForUser() {
        //category to be deleted
        Category category = createCategory(CATEGORY_NAME + categoryCounter++);
        appUserDao.save(category.getUser());
        category = categoryDao.saveAndFlush(category);
        assertTrue(category.getId() > 0);

        //category with different user
        Category category1 = createCategory(CATEGORY_NAME + categoryCounter++);
        AppUser appUser = createAppUser();
        appUser.setUsername("tudorgrig");
        appUser.setEmail("tudorgrigoriu@yahoo.com");
        appUser = appUserDao.save(appUser);
        category1.setUser(appUser);
        category1 = categoryDao.saveAndFlush(category1);

        //delete all categories for the first username
        categoryDao.deleteAllByUsername(category.getUser().getUsername());
        List<Category> categories = categoryDao.findByUsername(category.getUser().getUsername());
        assertEquals(0, categories.size());

        //check that the second category still exists
        Category foundCategory1 = categoryDao.findOne(category1.getId());
        assertNotNull(foundCategory1);
    }

    private Category createCategory(String categoryName) {

        Category category = new Category();
        category.setName(categoryName);
        category.setUser(applicationUser);
        return category;
    }

    private AppUser createAppUser() {

        AppUser appUser = new AppUser();
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setBirthdate(new Date());
        appUser.setUsername(USERNAME);
        appUser.setEmail(USERNAME + "@my-money-tracker.ro");
        appUser.setPassword("TEST_PASS");
        return appUser;
    }
}
