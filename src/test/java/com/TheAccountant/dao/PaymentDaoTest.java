package com.TheAccountant.dao;

import com.TheAccountant.model.payment.Payment;
import com.TheAccountant.model.payment.PaymentStripe;
import com.TheAccountant.model.payment.PaymentType;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.testUtil.TestMockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test Class for {@link PaymentDao} class
 *
 * Created by Florin on 10/21/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
public class PaymentDaoTest {

    private AppUser loggedUser;

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private PaymentStripeDao paymentStripeDao;

    @Before
    public void initialize() {
        loggedUser = this.createAndSaveAppUser();
    }

    @Test
    public void shouldSavePayment() {
        Payment payment = this.createPayment(loggedUser, this.createAndSavePaymentStripe("payment_dao_test"));
        Payment dbPayment = paymentDao.saveAndFlush(payment);
        assertNotNull("Saved payment should NOT be null!", dbPayment);
        assertTrue(dbPayment.getId() != 0);
        assertEquals(payment.getAmountCents(), dbPayment.getAmountCents());
        paymentDao.delete(dbPayment.getId());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotSavePayment() {
        Payment payment = this.createPayment(null, this.createAndSavePaymentStripe("payment_dao_test"));
        Payment dbPayment = paymentDao.saveAndFlush(payment);
        assertNull("Invalid saved payment should be null!", dbPayment);
    }

    @Test
    public void shouldFindByUserAndPaymentType() {
        Payment payment1 = this.createPayment(loggedUser, this.createAndSavePaymentStripe("payment_dao_test_1"));
        Payment payment2 = this.createPayment(loggedUser, this.createAndSavePaymentStripe("payment_dao_test_2"));
        Payment dbPayment1 = paymentDao.save(payment1);
        Payment dbPayment2 = paymentDao.save(payment2);
        assertNotNull("Saved payment 1 should NOT be null!", dbPayment1);
        assertNotNull("Saved payment 2 should NOT be null!", dbPayment2);
        List<Payment> paymentList = paymentDao.findByUserAndPaymentType(loggedUser.getUserId(), payment1.getPaymentType());
        assertNotNull("Payment list should NOT be null!", paymentList);
        assertEquals("Payment list should conatin 2 elements", 2, paymentList.size());
    }

    @Test
    public void shouldNotFindByUserAndPaymentType() {
        Payment payment = this.createPayment(loggedUser, this.createAndSavePaymentStripe("payment_dao_test"));
        Payment dbPayment = paymentDao.save(payment);
        assertNotNull("Saved payment should NOT be null!", dbPayment);
        List<Payment> paymentList = paymentDao.findByUserAndPaymentType(loggedUser.getUserId() + 1, payment.getPaymentType());
        assertTrue("List should be null or empty", paymentList == null || paymentList.isEmpty());
    }

    private Payment createPayment(AppUser appUser, PaymentStripe paymentStripe) {
        // Payment - table to register user payments
        Payment payment = new Payment();
        payment.setUser(appUser);
        payment.setPaymentStripe(paymentStripe);
        payment.setPaymentType(PaymentType.USER_LICENSE.name());
        payment.setAmountCents(499L);
        payment.setCurrency("USD");
        payment.setPaymentDescription("Payment TEST");
        payment.setCreationDate(new Date());
        return payment;
    }

    private AppUser createAndSaveAppUser() {
        AppUser appUser = TestMockUtil.createMockAppUser();
        appUserDao.save(appUser);
        return appUser;
    }

    private PaymentStripe createAndSavePaymentStripe(String chargeId) {
        PaymentStripe paymentStripe = TestMockUtil.createMockPaymentStripe();
        paymentStripe.setChargeId(chargeId);
        paymentStripe = paymentStripeDao.save(paymentStripe);
        return paymentStripe;
    }
}
