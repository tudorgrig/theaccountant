package com.TheAccountant.dao;

import com.TheAccountant.model.payment.PaymentStripe;
import com.TheAccountant.testUtil.TestMockUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test Class for {@link PaymentStripeDao} class
 *
 * Created by Florin on 10/21/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
public class PaymentStripeDaoTest {

    @Autowired
    private PaymentStripeDao paymentStripeDao;

    @Test
    public void shouldSavePaymentStripe() {
        PaymentStripe paymentStripe = TestMockUtil.createMockPaymentStripe();
        PaymentStripe dbPaymentStripe = paymentStripeDao.save(paymentStripe);
        assertNotNull("Saved PaymentStripe should NOT be null!", dbPaymentStripe);
        assertTrue(dbPaymentStripe.getId() != 0);
        assertEquals(paymentStripe.getAmountCents(), dbPaymentStripe.getAmountCents());
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldNotSavePaymentStripe() {
        PaymentStripe paymentStripe = TestMockUtil.createMockPaymentStripe();
        paymentStripe.setChargeId(null);
        PaymentStripe dbPaymentStripe = paymentStripeDao.saveAndFlush(paymentStripe);
        assertNull("Invalid PaymentStripe should be null!", dbPaymentStripe);
    }

    @Test
    public void shouldFindPaymentStripe() {
        PaymentStripe paymentStripe = TestMockUtil.createMockPaymentStripe();
        PaymentStripe dbPaymentStripe = paymentStripeDao.save(paymentStripe);
        assertNotNull("Saved PaymentStripe should NOT be null!", dbPaymentStripe);
        PaymentStripe foundPaymentStripe = paymentStripeDao.findOne(dbPaymentStripe.getId());
        assertNotNull("Found PaymentStripe should NOT be null!", foundPaymentStripe);
    }

}
