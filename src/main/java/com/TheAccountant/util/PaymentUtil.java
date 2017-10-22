package com.TheAccountant.util;

import com.TheAccountant.dao.PaymentStripeDao;
import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.model.payment.PaymentStripe;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Class containing useful methods for {@link com.TheAccountant.model.payment.Payment} entity management
 *
 * @author Florin Iacob
 */
@Component
public class PaymentUtil {

    public static final String SUCCESS_STATUS = "succeeded";

    @Autowired
    private PaymentStripeDao paymentStripeDao;

    @Value("${payment.license.amount.in.cents}")
    private Long paymentLicenseAmountInCents;

    /**
     * Create and insert in DB a new instance of {@link PaymentStripe} entity based on the received arguments
     *
     * @param chargeStripe: the instance of the response after a Stripe payment attempt
     * @param chargeDTO: the object containing details of the initiated charge
     * @return: the created instance
     */
    public PaymentStripe createPaymentStripeInstance(Charge chargeStripe, ChargeDTO chargeDTO) {
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

        return paymentStripe;
    }

    /**
     * Check the overall status of the transaction
     *
     * @param paid: true if the transaction was processed and the payment is done
     * @param transactionStatus: status of the transaction as string
     * @param refunded: true if the transaction was refunded
     * @param amountRefunded: amount from transaction refunded to the initiator
     * @return: an instance containing details for the transaction based on the received arguments
     */
    public ChargeDTO checkIfStripeTransactionApproved(Boolean paid, String transactionStatus, Boolean refunded, Long amountRefunded) {

        ChargeDTO resultDTO = new ChargeDTO();
        resultDTO.setPaymentApproved(true);
        resultDTO.setDescription("Transaction approved");

        if (paid == null || paid == false) {
            resultDTO.setPaymentApproved(false);
            resultDTO.setDescription("Payment was not approved!");
        } else if (transactionStatus == null || !transactionStatus.equals(SUCCESS_STATUS)) {
            resultDTO.setPaymentApproved(false);
            resultDTO.setDescription("Payment was not successful! Payment status: " + transactionStatus);
        } else if (refunded == true && amountRefunded>= paymentLicenseAmountInCents) {
            resultDTO.setPaymentApproved(false);
            resultDTO.setDescription("Payment was refunded!");
        }
        return resultDTO;
    }

}
