package com.TheAccountant.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.dao.CategoryDao;
import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.testUtil.TestMockUtil;
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

import com.TheAccountant.controller.exception.NotFoundException;
import com.TheAccountant.dao.AppUserDao;
import com.TheAccountant.dao.IncomeDao;
import com.TheAccountant.dto.category.CategoryDTO;
import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.util.ControllerUtil;

import javax.transaction.Transactional;

import static com.TheAccountant.controller.PaymentControllerTest.TEST_TOKEN;
import static org.junit.Assert.*;

/**
 * Test class for the CategoryController
 *
 * @author Florin Iacob
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
@SuppressWarnings("rawtypes")
public class CategoryControllerTest {
    
    private static final String CATEGORY_NAME = "Category1";
    private static final Long CATEGORY_ID = 777L;
    private static final String TEST_COLOUR = "stable";
    private static final double TEST_THRESHOLD = 0.0;

    @Autowired
    CategoryController categoryController;
    
    @Autowired
    AppUserDao appUserDao;
    
    @Autowired
    IncomeDao incomeDao;

    @Autowired
    CategoryDao categoryDao;

    private AppUser appUser;

    @Autowired
    private PaymentController paymentController;
    
    @Before
    public void initialize() {
    
        String username = "Florin";
        String email = "test@my-money-tracker.ro";
        appUser = createAppUser(username, email);
        ControllerUtil.setCurrentLoggedUser(username);
    }

    @Test
    public void shouldCreateCategory() {
    
        Category category = createCategory(CATEGORY_NAME);
        ResponseEntity responseEntity = categoryController.createCategory(category);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((CategoryDTO) responseEntity.getBody()).getId() > 0);
        assertEquals(TEST_COLOUR, ((CategoryDTO) responseEntity.getBody()).getColour());
        assertTrue(TEST_THRESHOLD == ((CategoryDTO) responseEntity.getBody()).getThreshold());
    }

    @Test
    public void shouldCreateCategoryWithThreshold() {

        // Only paid accounts can access Loan Module
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        chargeDTO.setStripeToken(TEST_TOKEN);
        paymentController.charge(chargeDTO);

        Category category = createCategory(CATEGORY_NAME);
        category.setThreshold(100.0);
        ResponseEntity responseEntity = categoryController.createCategory(category);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((CategoryDTO) responseEntity.getBody()).getId() > 0);
        assertTrue(100.0 == ((CategoryDTO) responseEntity.getBody()).getThreshold());
    }

    @Test
    public void shouldNotCreateCategoryWithThresholdForUnpaidAccount() {
        Category category = createCategory(CATEGORY_NAME);
        category.setThreshold(100.0);
        try {
            categoryController.createCategory(category);
        } catch (BadRequestException e) {
            assertEquals(e.getMessage(), "Category limit is allowed only for paid accounts!");
            return;
        }
        fail("category limit should be allowed only for paid accounts!");
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
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Category category = createCategory(CATEGORY_NAME + i);
            ResponseEntity responseEntity = categoryController.createCategory(category);
            assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
            assertTrue(((CategoryDTO) responseEntity.getBody()).getId() > 0);
            categories.add(category);
        }
        ResponseEntity responseEntity = categoryController.getAllCategories();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(5, ((List<CategoryDTO>) responseEntity.getBody()).size());
    }
    
    @Test
    public void shouldFindOneCategory() {
    
        Category category = createCategory(CATEGORY_NAME);
        categoryController.createCategory(category);
        ResponseEntity<?> found = categoryController.getCategory(category.getId());
        assertEquals(HttpStatus.OK, found.getStatusCode());
        assertTrue(found.getBody() != null);
    }
    
    @Test(expected = NotFoundException.class)
    public void shouldNotFindOneCategory() {
    
       categoryController.getCategory(CATEGORY_ID);
    }
    
    @Test
    public void shouldUpdateCategory() {
    
        String updatedName = "updatedCategoryName";
        Category category = createCategory(CATEGORY_NAME);
        ResponseEntity responseEntity = categoryController.createCategory(category);
        long id = ((CategoryDTO) responseEntity.getBody()).getId();
        Category toUpdatecategory = createCategory(updatedName);
        ResponseEntity updated = categoryController.updateCategory(id, toUpdatecategory);
        assertEquals(HttpStatus.NO_CONTENT, updated.getStatusCode());
        assertEquals("Category updated", updated.getBody());
        ResponseEntity updatedCategory = categoryController.getCategory(id);
        assertEquals(HttpStatus.OK, updatedCategory.getStatusCode());
    }

    @Test
    public void shouldUpdateCategoryWithThreshold() {

        // Only paid accounts can access Loan Module
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        chargeDTO.setStripeToken(TEST_TOKEN);
        paymentController.charge(chargeDTO);

        String updatedName = "categWithThreshold";
        Category category = createCategory(CATEGORY_NAME);
        ResponseEntity responseEntity = categoryController.createCategory(category);
        long id = ((CategoryDTO) responseEntity.getBody()).getId();
        Category toUpdatecategory = createCategory(updatedName);
        toUpdatecategory.setThreshold(100.0);
        ResponseEntity updated = categoryController.updateCategory(id, toUpdatecategory);
        assertEquals(HttpStatus.NO_CONTENT, updated.getStatusCode());
        assertEquals("Category updated", updated.getBody());
        ResponseEntity updatedCategory = categoryController.getCategory(id);
        assertEquals(HttpStatus.OK, updatedCategory.getStatusCode());
        assertTrue((100.0 - ((CategoryDTO) updatedCategory.getBody()).getThreshold()) < 0.1);
    }

    @Test
    public void shouldNotUpdateCategoryWithThresholdForUnpaidAccount() {
        String updatedName = "categWithThreshold";
        Category category = createCategory(CATEGORY_NAME);
        ResponseEntity responseEntity = categoryController.createCategory(category);
        long id = ((CategoryDTO) responseEntity.getBody()).getId();
        Category toUpdatecategory = createCategory(updatedName);
        toUpdatecategory.setThreshold(100.0);
        try {
            categoryController.updateCategory(id, toUpdatecategory);
        } catch (BadRequestException e) {
            assertEquals(e.getMessage(), "Category limit is allowed only for paid accounts!");
            return;
        }
        fail("Category limit should be allowed only for paid accounts!");
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotUpdateCategory() {
    
        String updatedName = "updatedCategoryName";
        Category toUpdatecategory = createCategory(updatedName);
        categoryController.updateCategory(-1l, toUpdatecategory);
    }
    
    @Test
    public void shouldDeleteCategory() {
    
        Category category = createCategory(CATEGORY_NAME);
        ResponseEntity responseEntity = categoryController.createCategory(category);
        ResponseEntity deletedEntity = categoryController.deleteCategory((((CategoryDTO) responseEntity.getBody()).getId()));
        assertEquals(HttpStatus.NO_CONTENT, deletedEntity.getStatusCode());
        assertEquals("Category deleted", deletedEntity.getBody());
        Category category1 = categoryDao.findOne(category.getId());
        assertNull(category1);
    }
    
    @Test(expected = NotFoundException.class)
    public void shouldNotDeleteCategory() {

        categoryController.deleteCategory(-1l);
    }
    
    private Category createCategory(String categoryName) {
    
        Category category = new Category();
        category.setName(categoryName);
        category.setColour(TEST_COLOUR);
        category.setThreshold(TEST_THRESHOLD);
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
        appUserDao.saveAndFlush(appUser);
        return appUser;
    }
}
