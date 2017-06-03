package com.TheAccountant.controller;

import com.TheAccountant.dao.AppUserDao;
import com.TheAccountant.dao.CounterpartyDao;
import com.TheAccountant.dto.loan.LoanDTO;
import com.TheAccountant.model.counterparty.Counterparty;
import com.TheAccountant.model.loan.Loan;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.util.ControllerUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.PortableInterceptor.ACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
/**
 * Created by tudor.grigoriu on 3/17/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-config.xml"})
@TestPropertySource(locations="classpath:application-test.properties")
public class LoanControllerTest {

    private static final String COUNTERPARTY_NAME = "COUNTERPARTY_NAME";
    private static final String EMAIL = "EMAIL";
    private static final String UPDATED_NAME = "UPDATED_COUNTERPARTY_NAME";
    private static final Double AMOUNT = Double.valueOf("2");
    private static final boolean ACTIVE = true;
    private static final String CURRENCY = "RON";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final Boolean RECEIVING = true;

    @Autowired
    private LoanController loanController;

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
    public void shouldCreate(){
        Counterparty counterparty = createCounterparty();
        counterparty = counterpartyDao.saveAndFlush(counterparty);
        Loan loan = createLoan();
        loan.setCounterparty(counterparty);
        ResponseEntity responseEntity = loanController.create(loan);
        LoanDTO result = (LoanDTO) responseEntity.getBody();
        assertTrue(result.getId() > 0 );
        assertEquals(loan.getActive(), result.getActive());
        assertEquals(loan.getCreationDate(), result.getCreationDate());
        assertEquals(loan.getAmount(), result.getAmount());
        assertEquals(loan.getCounterparty().getId(), result.getCounterparty().getId());
        assertEquals(loan.getCurrency(), result.getCurrency());
        assertEquals(loan.getReceiving(), result.getReceiving());
        assertEquals(loan.getDescription(), result.getDescription());
    }

    @Test
    public void shouldCreateWithDefaultCurrencyAmount() {
        Counterparty counterparty = createCounterparty();
        counterparty = counterpartyDao.saveAndFlush(counterparty);
        Loan loan = createLoan();
        loan.setCurrency("EUR");
        loan.setCounterparty(counterparty);
        ResponseEntity responseEntity = loanController.create(loan);
        LoanDTO result = (LoanDTO) responseEntity.getBody();
        assertEquals(CURRENCY, result.getDefaultCurrency());
        assertNotEquals(result.getAmount(), result.getDefaultCurrencyAmount());
    }

    @Test
    public void shouldCreateLoanAndCounterparty(){
        Counterparty counterparty = createCounterparty();
        Loan loan = createLoan();
        loan.setCounterparty(counterparty);
        ResponseEntity responseEntity = loanController.create(loan);
        LoanDTO result = (LoanDTO) responseEntity.getBody();
        assertTrue(result.getId() > 0 );
        assertEquals(loan.getActive(), result.getActive());
        assertEquals(loan.getCreationDate(), result.getCreationDate());
        assertEquals(loan.getAmount(), result.getAmount());
        assertEquals(loan.getCounterparty().getId(), result.getCounterparty().getId());
        assertEquals(loan.getCurrency(), result.getCurrency());
        assertEquals(loan.getReceiving(), result.getReceiving());
        assertEquals(loan.getDescription(), result.getDescription());
    }

    @Test
    public void shouldFindAll(){
        Counterparty counterparty = createCounterparty();
        counterpartyDao.saveAndFlush(counterparty);
        Loan loan = createLoan();
        loan.setCounterparty(counterparty);
        loanController.create(loan);
        ResponseEntity responseEntity = loanController.findAll(counterparty.getId());
        List<LoanDTO> resultList = (List<LoanDTO>) responseEntity.getBody();
        assertTrue(resultList.size() == 1);
        LoanDTO result = resultList.get(0);
        assertTrue(result.getId() > 0 );
        assertEquals(loan.getActive(), result.getActive());
        assertEquals(loan.getCreationDate(), result.getCreationDate());
        assertEquals(loan.getAmount(), result.getAmount());
        assertEquals(loan.getCounterparty().getId(), result.getCounterparty().getId());
        assertEquals(loan.getCurrency(), result.getCurrency());
        assertEquals(loan.getReceiving(), result.getReceiving());
        assertEquals(loan.getDescription(), result.getDescription());
    }

    @Test
    public void shouldDelete(){
        Counterparty counterparty = createCounterparty();
        counterparty = counterpartyDao.saveAndFlush(counterparty);
        Loan loan = createLoan();
        loan.setCounterparty(counterparty);
        loanController.create(loan);
        loanController.delete(loan.getId());
        ResponseEntity findAllResponseEntity = loanController.findAll(counterparty.getId());
        List<Counterparty> resultList = (List<Counterparty>) findAllResponseEntity.getBody();
        assertTrue(resultList == null || resultList.isEmpty());
    }

    @Test
    public void shouldUpdate() {

        Counterparty counterparty = createCounterparty();
        counterparty = counterpartyDao.saveAndFlush(counterparty);
        Loan loan = createLoan();
        loan.setCounterparty(counterparty);
        loanController.create(loan);
        loan.setDescription(UPDATED_NAME);
        ResponseEntity updated = loanController.update(loan.getId(), loan);
        assertEquals(HttpStatus.NO_CONTENT, updated.getStatusCode());
        assertEquals("Loan updated", updated.getBody());
        ResponseEntity responseEntity = loanController.findAll(counterparty.getId());
        List<LoanDTO> result = (List<LoanDTO>) responseEntity.getBody();
        assertEquals(UPDATED_NAME, result.get(0).getDescription());
    }

    @Test
    public void shouldUpdateDefaultCurrency() {
        Counterparty counterparty = createCounterparty();
        counterparty = counterpartyDao.saveAndFlush(counterparty);
        Loan loan = createLoan();
        loan.setCounterparty(counterparty);
        loanController.create(loan);
        loan.setCurrency("EUR");
        loanController.update(loan.getId(), loan);
        ResponseEntity responseEntity = loanController.findAll(counterparty.getId());
        List<LoanDTO> result = (List<LoanDTO>) responseEntity.getBody();
        assertEquals(CURRENCY, result.get(0).getDefaultCurrency());
        assertNotEquals(loan.getAmount(), result.get(0).getDefaultCurrencyAmount());
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
        appUser.setDefaultCurrency(Currency.getInstance(CURRENCY));
        appUserDao.saveAndFlush(appUser);
        return appUser;
    }

    private Loan createLoan() {
        Loan loan = new Loan();
        loan.setUser(appUser);
        loan.setCreationDate(new Timestamp(System.currentTimeMillis()));
        loan.setUntilDate(new Timestamp(System.currentTimeMillis()));
        loan.setActive(ACTIVE);
        loan.setAmount(AMOUNT);
        loan.setCurrency(CURRENCY);
        loan.setDescription(DESCRIPTION);
        loan.setReceiving(RECEIVING);
        return loan;
    }
}
