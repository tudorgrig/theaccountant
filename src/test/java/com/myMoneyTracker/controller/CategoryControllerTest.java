package com.myMoneyTracker.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import com.myMoneyTracker.dao.IncomeDao;
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
import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.ControllerUtil;

/**
 * Test class for the CategoryController
 *
 * @author Florin Iacob
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@SuppressWarnings("rawtypes")
public class CategoryControllerTest {

    private static final String CATEGORY_NAME = "Category1";

    @Autowired
    CategoryController categoryController;

    @Autowired
    AppUserDao appUserDao;

    @Autowired
    IncomeDao incomeDao;

    @Before
    public void initialize() {

        String username = "Florin";
        String email = "test@my-money-tracker.ro";
        createAppUser(username, email);
        ControllerUtil.setCurrentLoggedUser(username);
    }

    @After
    public void cleanUp() {

        incomeDao.deleteAll();
        incomeDao.flush();
        categoryController.deleteAll();
        appUserDao.deleteAll();
    }

    @Test
    public void shouldCreateCategory() {

        Category category = createCategory(CATEGORY_NAME);
        ResponseEntity responseEntity = categoryController.createCategory(category);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((Category) responseEntity.getBody()).getId() > 0);
    }

    @Test
    public void shouldFindEmptyListOfCategories() {

        ResponseEntity responseEntity = categoryController.getAllCategories();
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(null, responseEntity.getBody());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFindAllCategeories() {

        for (int i = 0; i < 5; i++) {
            Category category = createCategory(CATEGORY_NAME + i);
            ResponseEntity responseEntity = categoryController.createCategory(category);
            assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
            assertTrue(((Category) responseEntity.getBody()).getId() > 0);
        }
        ResponseEntity responseEntity = categoryController.getAllCategories();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(5, ((List<Category>) responseEntity.getBody()).size());
    }

    @Test
    public void shouldFindOneCategory() {

        Category category = createCategory(CATEGORY_NAME);
        categoryController.createCategory(category);
        ResponseEntity<?> found = categoryController.getCategory(CATEGORY_NAME);
        assertEquals(HttpStatus.OK, found.getStatusCode());
        assertTrue(found.getBody() != null);
    }

    @Test
    public void shouldNotFindOneCategory() {

        Category category = createCategory(CATEGORY_NAME);
        categoryController.createCategory(category);
        ResponseEntity<?> found = categoryController.getCategory(CATEGORY_NAME + "extra");
        assertEquals(HttpStatus.NOT_FOUND, found.getStatusCode());
    }

    @Test
    public void shouldUpdateCategory() {

        String updatedName = "updatedCategoryName";
        Category category = createCategory(CATEGORY_NAME);
        ResponseEntity responseEntity = categoryController.createCategory(category);
        long id = ((Category) responseEntity.getBody()).getId();
        Category toUpdatecategory = createCategory(updatedName);
        ResponseEntity updated = categoryController.updateCategory(id, toUpdatecategory);
        assertEquals(HttpStatus.NO_CONTENT, updated.getStatusCode());
        assertEquals("Category updated", updated.getBody());
        ResponseEntity updatedCategory = categoryController.getCategory(updatedName);
        assertEquals(HttpStatus.OK, updatedCategory.getStatusCode());
    }

    @Test
    public void shouldNotUpdateCategory() {

        String updatedName = "updatedCategoryName";
        Category category = createCategory(CATEGORY_NAME);
        ResponseEntity responseEntity = categoryController.createCategory(category);
        long id = ((Category) responseEntity.getBody()).getId();
        Category toUpdatecategory = createCategory(updatedName);
        ResponseEntity updated = categoryController.updateCategory(id + 1, toUpdatecategory);
        assertEquals(HttpStatus.NOT_FOUND, updated.getStatusCode());
        assertEquals("Category not found", updated.getBody());
    }

    @Test
    public void shouldDeleteCategory() {

        Category category = createCategory(CATEGORY_NAME);
        ResponseEntity responseEntity = categoryController.createCategory(category);
        ResponseEntity deletedEntity = categoryController.deleteCategory((((Category) responseEntity.getBody()).getId()));
        assertEquals(HttpStatus.NO_CONTENT, deletedEntity.getStatusCode());
        assertEquals("Category deleted", deletedEntity.getBody());
    }

    @Test
    public void shouldNotDeleteCategory() {

        Category category = createCategory(CATEGORY_NAME);
        ResponseEntity responseEntity = categoryController.createCategory(category);
        ResponseEntity deletedEntity = categoryController.deleteCategory((((Category) responseEntity.getBody()).getId() + 1));
        assertEquals(HttpStatus.NOT_FOUND, deletedEntity.getStatusCode());
    }

    private Category createCategory(String categoryName) {

        Category category = new Category();
        category.setName(categoryName);
        return category;
    }

    private AppUser createAppUser(String username, String email) {

        AppUser appUser = new AppUser();
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setBirthdate(new Date());
        appUser.setUsername(username);
        appUser.setEmail(email);
        appUserDao.save(appUser);
        return appUser;
    }
}
