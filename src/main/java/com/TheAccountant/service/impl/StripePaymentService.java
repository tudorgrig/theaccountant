package com.TheAccountant.service.impl;

import com.TheAccountant.controller.PaymentController;
import com.TheAccountant.dao.PaymentDao;
import com.TheAccountant.dao.PaymentStripeDao;
import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.model.payment.Payment;
import com.TheAccountant.model.payment.PaymentStripe;
import com.TheAccountant.model.payment.PaymentType;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.service.PaymentService;
import com.TheAccountant.service.exception.ServiceException;
import com.TheAccountant.util.PaymentUtil;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Florin on 7/31/2017.
 */
@Service
@Transactional
public class StripePaymentService implements PaymentService {

    private static final Logger LOG = Logger.getLogger(PaymentController.class.getName());

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${payment.license.amount.in.cents}")
    private Long paymentLicenseAmountInCents;

    @Value("${payment.license.currency}")
    private String paymentLicenseCurrency;

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private PaymentUtil paymentUtil;

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
            LOG.log(Level.SEVERE, "Problems verifying information about provided data! Error: " + e.getMessage());
            throw new ServiceException("Problems verifying information about provided data! Error: " + e.getMessage());
        } catch (InvalidRequestException e) {
            LOG.log(Level.SEVERE, "Invalid Payment attempt! Error: " + e.getMessage());
            throw new ServiceException("Invalid Payment attempt! Error: " + e.getMessage());
        } catch (APIConnectionException e) {
            LOG.log(Level.SEVERE, "Invalid payment API access! Error: " + e.getMessage());
            throw new ServiceException("Invalid payment API access! Error: " + e.getMessage());
        } catch (CardException e) {
            LOG.log(Level.SEVERE, "Problems validating the provided card information! Error: " + e.getMessage());
            throw new ServiceException("Problems validating the provided card information! Error: " + e.getMessage());
        } catch (APIException e) {
            LOG.log(Level.SEVERE, "Problems accessing payment support! Error: " + e.getMessage());
            throw new ServiceException("Problems accessing payment support! Error: " + e.getMessage());
        }

        PaymentStripe paymentStripe = paymentUtil.createPaymentStripeInstance(chargeStripe, chargeDTO);

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
        ChargeDTO chargeResponse  = paymentUtil.checkIfStripeTransactionApproved(chargeStripe.getPaid(), chargeStripe.getStatus(), chargeStripe.getRefunded(), chargeStripe.getAmountRefunded());
        chargeResponse.setAmount(chargeStripe.getAmount());
        chargeResponse.setCurrency(ChargeDTO.Currency.valueOf(chargeStripe.getCurrency().toUpperCase()));
        return chargeResponse;
    }

    @Override
    public ChargeDTO getPaymentStatus(PaymentType paymentType) throws ServiceException {
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
                    resultDTO = paymentUtil.checkIfStripeTransactionApproved(paymentStripe.getPaid(), paymentStripe.getStatus(), paymentStripe.getRefunded(), paymentStripe.getAmountCentsRefunded());
                }
            }
        }

        return resultDTO;
    }

}
