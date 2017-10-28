package com.TheAccountant.controller;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.dao.AppUserDao;
import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.testUtil.TestMockUtil;
import com.TheAccountant.util.ControllerUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Test class for {@link PaymentController}
 *
 * @author Florin on 10/21/2017
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class PaymentControllerTest {

    public static final String TEST_TOKEN = "tok_amex";
    public static final String REPEATED_TOKEN = "tok_1BFfrYJIoRuhDplFvaorVr8l";

    public static final String LOGGED_USERNAME = "stripe_payment_service_test";

    private AppUser applicationUser;

    @Autowired
    private PaymentController paymentController;

    @Autowired
    private AppUserDao appUserDao;

    @Before
    public void setup() {

        applicationUser = createAndSaveAppUser(LOGGED_USERNAME);
        ControllerUtil.setCurrentLoggedUser(LOGGED_USERNAME);
    }

    @Test
    public void shouldCharge() {
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        chargeDTO.setStripeToken(TEST_TOKEN);
        ResponseEntity responseEntity = paymentController.charge(chargeDTO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ChargeDTO);
        ChargeDTO responseChargeDTO = (ChargeDTO) responseEntity.getBody();
        assertEquals(true, responseChargeDTO.getPaymentApproved());
        assertEquals(chargeDTO.getAmount(), responseChargeDTO.getAmount());
    }

    @Test
    public void shouldNotCharge() {
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        chargeDTO.setStripeToken(REPEATED_TOKEN);
        boolean exceptionThrown = false;
        try {
            paymentController.charge(chargeDTO);
        } catch (BadRequestException e) {
            e.printStackTrace();
            exceptionThrown = true;
            assertTrue(e.getMessage().toLowerCase().contains("cannot use a stripe token more than once"));
        }
        assertTrue("Invalid charge attempt should throw exception!", exceptionThrown);
    }

    @Test
    public void shouldGetSuccessStatus() {
        // Charge the user
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        chargeDTO.setStripeToken(TEST_TOKEN);
        ResponseEntity responseEntity = paymentController.charge(chargeDTO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Chack charge status
        responseEntity = paymentController.getPaymentStatusForUser();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ChargeDTO);
        ChargeDTO responseChargeDTO = (ChargeDTO) responseEntity.getBody();
        assertEquals(true, responseChargeDTO.getPaymentApproved());
        assertTrue(responseChargeDTO.getDescription().toLowerCase().contains("transaction approved"));
    }

    @Test
    public void shouldNotGetSuccessStatus() {
        // Charge the user
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        chargeDTO.setStripeToken(REPEATED_TOKEN);
        try {
            paymentController.charge(chargeDTO);
        } catch (BadRequestException e) {}

        // Chack charge status
        ResponseEntity responseEntity = paymentController.getPaymentStatusForUser();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ChargeDTO);
        ChargeDTO responseChargeDTO = (ChargeDTO) responseEntity.getBody();
        assertEquals(false, responseChargeDTO.getPaymentApproved());
        assertTrue(responseChargeDTO.getDescription() == null || !responseChargeDTO.getDescription().toLowerCase().contains("transaction approved"));
    }

    private AppUser createAndSaveAppUser(String loggedUsername) {
        AppUser appUser = TestMockUtil.createMockAppUser();
        appUser.setUsername(loggedUsername);
        applicationUser = appUserDao.save(appUser);
        return appUser;
    }
}
