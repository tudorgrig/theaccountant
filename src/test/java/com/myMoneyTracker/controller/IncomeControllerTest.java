package com.myMoneyTracker.controller;

import com.myMoneyTracker.dao.CategoryDao;
import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.income.Income;
import com.myMoneyTracker.model.user.AppUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tudor Grigoriu
 * Test class for the income controller
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
public class IncomeControllerTest {

    @Autowired
    IncomeController incomeController;

    @Autowired
    AppUserController appUserController;

    @Autowired
    CategoryDao categoryDao;

    @Before
    public void deleteAllIncomes() {

        incomeController.deleteAll();
        appUserController.deleteAll();

    }

    @Test
    public void shouldCreateIncome() {

        Income income = createIncome();
        ResponseEntity responseEntity = incomeController.createIncome(income);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((Income) responseEntity.getBody()).getId() > 0);
    }

    @Test
    public void shouldNotCreateIncome() {

        Income income = createIncome();
        income.setName(null);
        ResponseEntity responseEntity = incomeController.createIncome(income);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void shouldListAllIncomes() {

        Income income = createIncome();
        ResponseEntity responseEntity = incomeController.createIncome(income);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        responseEntity = incomeController.listAllIncomes();
        assertEquals(1, ((List<Income>) responseEntity.getBody()).size());
        Income result = ((List<Income>) responseEntity.getBody()).get(0);
        assertEquals(income.getName(), result.getName());
    }

    @Test
    public void shouldFindById() {

        Income income = createIncome();
        ResponseEntity responseEntity = incomeController.createIncome(income);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        responseEntity = incomeController.findIncome(((Income) responseEntity.getBody()).getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Income found = (Income) responseEntity.getBody();
        assertEquals(income.getName(), found.getName());
    }

    @Test
    public void shouldNotFindById() {

        ResponseEntity responseEntity = incomeController.findIncome(new Random().nextLong());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void shouldFindByUserId() {

        Income income = createIncome();
        ResponseEntity responseEntity = incomeController.createIncome(income);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        responseEntity = incomeController.findByUserId(income.getUser().getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, ((List<Income>) responseEntity.getBody()).size());
        Income result = ((List<Income>) responseEntity.getBody()).get(0);
        assertEquals(income.getName(), result.getName());
    }

    @Test
    public void shouldNotFindByUserId() {

        ResponseEntity responseEntity = incomeController.findByUserId(new Random().nextLong());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    private Income createIncome() {

        Income income = new Income();
        income.setName("name1");
        income.setDescription("description1");
        income.setAmount(new Double(222.222));
        income.setCreationDate(new Timestamp(System.currentTimeMillis()));
        return income;
    }

    private AppUser createAppUser() {

        Random random = new Random();
        AppUser appUser = new AppUser();
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setBirthdate(new Date());
        appUser.setUsername("florin.e.iacob");
        appUser.setEmail("my-money-tracker@gmail.com");
        return appUser;
    }

    private Category createCategory() {

        Category category = new Category();
        category.setName("Florin");
        categoryDao.saveAndFlush(category);
        return category;
    }
}
