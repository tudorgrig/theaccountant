package com.myMoneyTracker.controller;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tudor Grigoriu
 * Test class for the income controller
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"/spring-config.xml"})
public class IncomeControllerTest {

    //    @Autowired
    IncomeController incomeController;

    //    @Autowired
    AppUserController appUserController;

    //    @Before
    //    public void deleteAllIncomes(){
    //        incomeController.deleteAll();
    //    }

    //    @Test
    //    public void shouldCreateIncome(){
    //        AppUser appUser = createAppUser(FIRST_NAME);
    //        ResponseEntity responseEntity = appUserController.createAppUser(appUser);
    //        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    //        assertTrue(((AppUser)responseEntity.getBody()).getId() > 0);
    //    }

    //    private Income createIncome() {
    //        Income income = new Income();
    //        income.setName("name1");
    //        income.setDescription("description1");
    //        income.setAmount(new Double(222.222));
    //        income.setCreationDate(new Timestamp(System.currentTimeMillis()));
    //        income.setUser(createAppUser());
    //        income.setCategory(createCategory());
    //        return income;
    //    }

    private AppUser createAppUser() {

        AppUser appUser = new AppUser();
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setBirthdate(new Date());
        appUser.setEmail("my-money-tracker@gmail.com");
        ResponseEntity responseEntity = appUserController.createAppUser(appUser);
        return (AppUser) responseEntity.getBody();
    }

    //    private Category createCategory() {
    //        Category category = new Category();
    //        category.setName("Florin");
    //        categoryDao.save(category);
    //        return category;
    //    }
}
