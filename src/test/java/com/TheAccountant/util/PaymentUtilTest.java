package com.TheAccountant.util;

import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.model.payment.PaymentStripe;
import com.TheAccountant.testUtil.TestMockUtil;
import com.stripe.model.Charge;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for {@link PaymentUtil}
 *
 * @author Florin on 10/21/2017
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class PaymentUtilTest {

    @Autowired
    private PaymentUtil paymentUtil;

    @Test
    public void shouldCreatePaymentStripeInstance() {
        Charge chargeStripe = TestMockUtil.createMockChargeStripe();
        ChargeDTO chargeDTO = TestMockUtil.createMockChargeDTO();
        PaymentStripe paymentStripe = paymentUtil.createPaymentStripeInstance(chargeStripe, chargeDTO);
        assertNotNull("Instance should NOT be null", paymentStripe);
        assertEquals(chargeStripe.getId(), paymentStripe.getChargeId());
        assertEquals(chargeStripe.getAmount(), paymentStripe.getAmountCents());
        assertEquals(chargeStripe.getAmountRefunded(), paymentStripe.getAmountCentsRefunded());
        assertEquals(chargeStripe.getPaid(), paymentStripe.getPaid());
        assertEquals(chargeStripe.getRefunded(), paymentStripe.getRefunded());
        assertEquals(chargeStripe.getCurrency(), paymentStripe.getCurrency());
        assertEquals(chargeDTO.getStripeEmail(), paymentStripe.getChargeEmail());
    }

    @Test
    public void shouldCheckIfStripeTransactionApproved() {

        ChargeDTO result = paymentUtil.checkIfStripeTransactionApproved(true, PaymentUtil.SUCCESS_STATUS, false, 0L);
        assertEquals("Transaction should be approved", result.getPaymentApproved(), true);

        result = paymentUtil.checkIfStripeTransactionApproved(false, PaymentUtil.SUCCESS_STATUS, false, 0L);
        assertEquals("Transaction with paid=false should NOT be approved", result.getPaymentApproved(), false);

        result = paymentUtil.checkIfStripeTransactionApproved(true, "rejected", false, 0L);
        assertEquals("Transaction with wrong status should NOT be approved", result.getPaymentApproved(), false);

        result = paymentUtil.checkIfStripeTransactionApproved(true, PaymentUtil.SUCCESS_STATUS, true, 1000L);
        assertEquals("Transaction refunded should NOT be approved", result.getPaymentApproved(), false);
    }
}
