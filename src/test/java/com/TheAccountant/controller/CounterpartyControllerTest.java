package com.TheAccountant.controller;

import com.TheAccountant.dao.AppUserDao;
import com.TheAccountant.dao.CounterpartyDao;
import com.TheAccountant.model.counterparty.Counterparty;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.util.ControllerUtil;
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

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by tudor.grigoriu on 3/17/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
@SuppressWarnings("rawtypes")
public class CounterpartyControllerTest {

    private static final String COUNTERPARTY_NAME = "COUNTERPARTY_NAME";
    private static final String EMAIL = "EMAIL";
    private static final String UPDATED_NAME = "UPDATED_COUNTERPARTY_NAME";
    @Autowired
    private CounterpartyController counterpartyController;

    @Autowired
    AppUserDao appUserDao;

    @Autowired
    CounterpartyDao counterpartyDao;

    private AppUser appUser;

    @Before
    public void initialize() {

        String username = "Florin";
        String email = "test@my-money-tracker.ro";
        appUser = createAppUser(username, email);
        ControllerUtil.setCurrentLoggedUser(username);
    }

    @After
    public void cleanUp() {
        if(appUser != null && appUser.getUserId() != 0) {
            appUserDao.delete(appUser.getUserId());
            appUserDao.flush();
        }
    }

    @Test
    public void shouldCreateCounterparty(){
        Counterparty counterparty = createCounterparty();
        ResponseEntity responseEntity = counterpartyController.create(counterparty);
        Counterparty result = (Counterparty) responseEntity.getBody();
        assertTrue(result.getId() > 0 );
        assertEquals(counterparty.getName(), result.getName());
        assertEquals(appUser.getUsername(), result.getUser().getUsername());
        assertEquals(counterparty.getEmail(), result.getEmail());
    }

    @Test
    public void shouldFindAllCounterparties(){
        Counterparty counterparty = createCounterparty();
        counterpartyDao.saveAndFlush(counterparty);
        ResponseEntity responseEntity = counterpartyController.findAll();
        List<Counterparty> resultList = (List<Counterparty>) responseEntity.getBody();
        assertTrue(resultList.size() == 1);
        assertEquals(counterparty.getName(), resultList.get(0).getName());
        assertEquals(counterparty.getEmail(), resultList.get(0).getEmail());
        assertEquals(counterparty.getUser().getUsername(), resultList.get(0).getUser().getUsername());
    }

    @Test
    public void shouldDeleteCounterparty(){
        Counterparty counterparty = createCounterparty();
        counterparty = counterpartyDao.saveAndFlush(counterparty);
        counterpartyController.delete(counterparty.getId());
        ResponseEntity findAllResponseEntity = counterpartyController.findAll();
        List<Counterparty> resultList = (List<Counterparty>) findAllResponseEntity.getBody();
        assertTrue(resultList.isEmpty());
    }

    @Test
    public void shouldUpdateCounterparty() {

        Counterparty counterparty = createCounterparty();
        ResponseEntity responseEntity = counterpartyController.create(counterparty);
        long id = ((Counterparty) responseEntity.getBody()).getId();
        Counterparty toUpdatecategory = createCounterparty();
        toUpdatecategory.setName(UPDATED_NAME);
        ResponseEntity updated = counterpartyController.update(id, toUpdatecategory);
        assertEquals(HttpStatus.NO_CONTENT, updated.getStatusCode());
        assertEquals("Counterparty updated", updated.getBody());
        Counterparty updatedCategory = counterpartyDao.findOne(id);
        assertEquals(UPDATED_NAME, updatedCategory.getName());
    }



    private Counterparty createCounterparty() {
        Counterparty counterparty = new Counterparty();
        counterparty.setUser(appUser);
        counterparty.setName(COUNTERPARTY_NAME);
        counterparty.setEmail(EMAIL);
        return counterparty;
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
