package com.TheAccountant.testUtil;

import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.model.payment.PaymentStripe;
import com.TheAccountant.model.user.AppUser;
import com.stripe.model.Charge;
import com.stripe.model.ChargeOutcome;

import java.util.Date;

public class TestMockUtil {

    public static PaymentStripe createMockPaymentStripe() {
        // PaymentStripe - details for charging with stripe
        PaymentStripe paymentStripe = new PaymentStripe();
        paymentStripe.setChargeId("chargeId");
        paymentStripe.setCreationDate(new Date());
        paymentStripe.setStatus("succeeded");
        paymentStripe.setPaid(true);
        paymentStripe.setRefunded(false);
        paymentStripe.setAmountCents(499L);
        paymentStripe.setAmountCentsRefunded(0L);
        paymentStripe.setCurrency("USD");
        paymentStripe.setChargeOutcome("{}");
        paymentStripe.setChargeEmail("charge@email.com");
        paymentStripe.setPaymentDescription("Payment Stripe TEST!");

        return paymentStripe;
    }

    public static AppUser createMockAppUser() {
        AppUser appUser = new AppUser();
        appUser.setFirstName("MockName");
        appUser.setSurname("MockSurname");
        appUser.setPassword("MockPassword");
        appUser.setUsername("mock_user");
        appUser.setBirthdate(new Date());
        appUser.setEmail("mock_user@test.com");
        return appUser;
    }

    public static Charge createMockChargeStripe() {
        Charge chargeStripe = new Charge();
        chargeStripe.setId("mock_id");
        chargeStripe.setPaid(true);
        chargeStripe.setCurrency("USD");
        chargeStripe.setAmount(499L);
        chargeStripe.setDescription("Mock charge stripe");
        chargeStripe.setRefunded(false);
        chargeStripe.setStatus("succeeded");
        chargeStripe.setApplication("mock_application_id");
        chargeStripe.setAmountRefunded(10L);
        chargeStripe.setApplicationFee("mock_application_fee");
        chargeStripe.setCaptured(true);
        chargeStripe.setCreated(System.currentTimeMillis());
        chargeStripe.setOutcome(new ChargeOutcome());
        return chargeStripe;
    }

    public static ChargeDTO createMockChargeDTO() {
        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setDescription("Mock chargeDTO");
        chargeDTO.setAmount(499L);
        chargeDTO.setCurrency(ChargeDTO.Currency.USD);
        chargeDTO.setStripeEmail("mock@charge.com");
        chargeDTO.setStripeToken("mock_stripe_token");
        return chargeDTO;
    }
}
