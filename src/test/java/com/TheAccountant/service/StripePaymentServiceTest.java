package com.TheAccountant.service;

import com.TheAccountant.dao.AppUserDao;
import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.model.payment.PaymentType;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.service.exception.ServiceException;
import com.TheAccountant.service.impl.StripePaymentService;
import com.TheAccountant.testUtil.TestMockUtil;
import com.TheAccountant.util.ControllerUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link StripePaymentService}
 *
 * @author Florin on 10/21/2017
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class StripePaymentServiceTest {

    public static final String TEST_TOKEN = "tok_amex";
    public static final String REPEATED_TOKEN = "tok_1BFfrYJIoRuhDplFvaorVr8l";

    public static final String LOGGED_USERNAME = "stripe_payment_service_test";

    private AppUser applicationUser;

    @Autowired
    private PaymentService stripePaymentService;

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
        ChargeDTO resultChargeDTO = null;
        try {
            resultChargeDTO = stripePaymentService.charge(chargeDTO, PaymentType.USER_LICENSE);
        } catch (ServiceException e) {
            e.printStackTrace();
            Assert.fail("Transaction attempt should NOT be with exception!");
        }
        assertNotNull(resultChargeDTO);
        assertEquals(true, resultChargeDTO.getPaymentApproved());
        assertEquals(chargeDTO.getAmount(), resultChargeDTO.getAmount());
    }

    @Test
    public void shouldNotChargeWithInvalidToken() {
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        chargeDTO.setStripeToken("tok_invalid");
        boolean exceptionThrown = false;
        try {
            stripePaymentService.charge(chargeDTO, PaymentType.USER_LICENSE);
        } catch (ServiceException e) {
            e.printStackTrace();
            exceptionThrown = true;
            assertTrue(e.getMessage().toLowerCase().contains("no such token"));
        }
        assertTrue("Invalid charge attempt should throw exception!", exceptionThrown);
    }

    @Test
    public void shouldNotChargeWithRepeatedToken() {
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        chargeDTO.setStripeToken(REPEATED_TOKEN);
        boolean exceptionThrown = false;
        try {
            stripePaymentService.charge(chargeDTO, PaymentType.USER_LICENSE);
        } catch (ServiceException e) {
            e.printStackTrace();
            exceptionThrown = true;
            assertTrue(e.getMessage().toLowerCase().contains("cannot use a stripe token more than once"));
        }
        assertTrue("Invalid charge attempt should throw exception!", exceptionThrown);
    }

    @Test
    public void shouldGetSuccessStatus() throws ServiceException {
        // Charge the user
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        chargeDTO.setStripeToken(TEST_TOKEN);
        stripePaymentService.charge(chargeDTO, PaymentType.USER_LICENSE);

        // Get status
        ChargeDTO resultChargeDTO = null;
        try {
            resultChargeDTO = stripePaymentService.getPaymentStatus(PaymentType.USER_LICENSE);
        } catch (ServiceException e) {
            e.printStackTrace();
            Assert.fail("Request for status should NOT be with exception!");
        }
        assertNotNull(resultChargeDTO);
        assertEquals(true, resultChargeDTO.getPaymentApproved());
        assertTrue(resultChargeDTO.getDescription().toLowerCase().contains("transaction approved"));
    }

    @Test
    public void shouldNotGetSuccessStatus() throws ServiceException {
        // Charge user with an invalid token
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        chargeDTO.setStripeToken("tok_invalid");
        try {
            stripePaymentService.charge(chargeDTO, PaymentType.USER_LICENSE);
        } catch (ServiceException e) {}

        // Check status
        ChargeDTO resultChargeDTO = null;
        try {
            resultChargeDTO = stripePaymentService.getPaymentStatus(PaymentType.USER_LICENSE);
        } catch (ServiceException e) {
            e.printStackTrace();
            Assert.fail("Request for status should NOT be with exception!");
        }
        assertNotNull(resultChargeDTO);
        assertEquals(false, resultChargeDTO.getPaymentApproved());
        assertTrue(resultChargeDTO.getDescription() == null || !resultChargeDTO.getDescription().toLowerCase().contains("transaction approved"));
    }

    private AppUser createAndSaveAppUser(String loggedUsername) {
        AppUser appUser = TestMockUtil.createMockAppUser();
        appUser.setUsername(loggedUsername);
        applicationUser = appUserDao.save(appUser);
        return appUser;
    }
}
