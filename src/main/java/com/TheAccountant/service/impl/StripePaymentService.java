package com.TheAccountant.service.impl;

import com.TheAccountant.dao.PaymentDao;
import com.TheAccountant.dao.PaymentStripeDao;
import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.model.payment.Payment;
import com.TheAccountant.model.payment.PaymentStripe;
import com.TheAccountant.model.payment.PaymentType;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.service.PaymentService;
import com.TheAccountant.service.exception.ServiceException;
import com.TheAccountant.util.UserUtil;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Florin on 7/31/2017.
 */
@Service
@Transactional
public class StripePaymentService implements PaymentService {

    private static final String SUCCESS_STATUS = "succeeded";

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${payment.license.amount.in.cents}")
    private Long paymentLicenseAmountInCents;

    @Value("${payment.license.currency}")
    private String paymentLicenseCurrency;

    @Autowired
    private PaymentStripeDao paymentStripeDao;

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private UserUtil userUtil;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public ChargeDTO charge(ChargeDTO chargeDTO, PaymentType paymentType) throws ServiceException {
        Map<String, Object> chargeParams = new HashMap<>();
        if (PaymentType.USER_LICENSE.equals(paymentType)) {
            chargeParams.put("amount", paymentLicenseAmountInCents);
            chargeParams.put("currency", paymentLicenseCurrency);
        } else {
            throw new ServiceException("Payment type not recognized!");
        }
        chargeParams.put("description", chargeDTO.getDescription());
        chargeParams.put("source", chargeDTO.getStripeToken());
        Charge chargeStripe = null;
        try {
            chargeStripe = Charge.create(chargeParams);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new ServiceException("Problems verifying informations about provided data! Error: " + e.getMessage());
        } catch (InvalidRequestException e) {
            e.printStackTrace();
            throw new ServiceException("Invalid Payment attempt! Error: " + e.getMessage());
        } catch (APIConnectionException e) {
            e.printStackTrace();
            throw new ServiceException("Invalid payment API access! Error: " + e.getMessage());
        } catch (CardException e) {
            e.printStackTrace();
            throw new ServiceException("Problems validating the provided card information! Error: " + e.getMessage());
        } catch (APIException e) {
            e.printStackTrace();
            throw new ServiceException("Problems accessing payment support! Error: " + e.getMessage());
        }

        // PaymentStripe - details for charging with stripe
        PaymentStripe paymentStripe = new PaymentStripe();
        paymentStripe.setChargeId(chargeStripe.getId());
        paymentStripe.setCreationDate(new Date(chargeStripe.getCreated()));
        paymentStripe.setStatus(chargeStripe.getStatus());
        paymentStripe.setPaid(chargeStripe.getPaid());
        paymentStripe.setRefunded(chargeStripe.getRefunded());
        paymentStripe.setAmountCents(chargeStripe.getAmount());
        paymentStripe.setAmountCentsRefunded(chargeStripe.getAmountRefunded());
        paymentStripe.setCurrency(chargeStripe.getCurrency());
        paymentStripe.setChargeOutcome(chargeStripe.getOutcome().toJson());
        paymentStripe.setChargeEmail(chargeDTO.getStripeEmail());
        paymentStripe.setPaymentDescription(chargeStripe.getDescription());
        paymentStripe = paymentStripeDao.save(paymentStripe);

        // Payment - table to register user payments
        Payment payment = new Payment();
        payment.setUser(userUtil.extractLoggedAppUserFromDatabase());
        payment.setPaymentStripe(paymentStripe);
        payment.setPaymentType(paymentType.name());
        payment.setAmountCents(chargeStripe.getAmount());
        payment.setCurrency(chargeStripe.getCurrency());
        payment.setPaymentDescription(chargeStripe.getDescription());
        payment.setCreationDate(new Date());
        paymentDao.save(payment);

        // create response
        ChargeDTO chargeResponse  = this.checkIfStripeTransactionApproved(chargeStripe.getPaid(), chargeStripe.getStatus(), chargeStripe.getRefunded(), chargeStripe.getAmountRefunded());
        chargeResponse.setAmount(chargeStripe.getAmount());
        chargeResponse.setCurrency(ChargeDTO.Currency.valueOf(chargeStripe.getCurrency().toUpperCase()));
        return chargeResponse;
    }

    @Override
    public ChargeDTO getPaymentStatusForUser(PaymentType paymentType) throws ServiceException {
        ChargeDTO resultDTO = new ChargeDTO();
        AppUser sessionUser = userUtil.extractLoggedAppUserFromDatabase();
        if (sessionUser == null) {
            resultDTO.setPaymentApproved(false);
            resultDTO.setDescription("Cannot check payment status for invalid session user!");
        } else {
            List<Payment> paymentList = paymentDao.findByUserAndPaymentType(sessionUser.getUserId(), paymentType.name());
            if (paymentList == null || paymentList.isEmpty()) {
                resultDTO.setPaymentApproved(false);
            } else {
                Payment payment = paymentList.get(0);
                PaymentStripe paymentStripe = payment.getPaymentStripe();
                if (paymentStripe == null) {
                    resultDTO.setPaymentApproved(false);
                    resultDTO.setDescription("No provider payment registered for current user!");
                } else {
                    resultDTO = this.checkIfStripeTransactionApproved(paymentStripe.getPaid(), paymentStripe.getStatus(), paymentStripe.getRefunded(), paymentStripe.getAmountCentsRefunded());
                }
            }
        }

        return resultDTO;
    }


    /**
     * Check the overall status of the transaction
     *
     * @param paid
     * @param transactionStatus
     * @param refunded
     * @param amountRefunded
     * @return
     */
    private ChargeDTO checkIfStripeTransactionApproved(Boolean paid, String transactionStatus, Boolean refunded, Long amountRefunded) {

        ChargeDTO resultDTO = new ChargeDTO();
        resultDTO.setPaymentApproved(true);
        resultDTO.setDescription("Transaction approved");

        if (paid == null || paid == false) {
            resultDTO.setPaymentApproved(false);
            resultDTO.setDescription("Payment was not approved!");
        } else if (transactionStatus == null || !transactionStatus.equals(SUCCESS_STATUS)) {
            resultDTO.setPaymentApproved(false);
            resultDTO.setDescription("Payment was not successful! Payment status: " + transactionStatus);
        } else if (refunded == true && amountRefunded > 100L) {
            resultDTO.setPaymentApproved(false);
            resultDTO.setDescription("Payment was refunded!");
        }
        return resultDTO;
    }
}
