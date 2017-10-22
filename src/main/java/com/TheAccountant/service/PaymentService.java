package com.TheAccountant.service;

import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.model.payment.PaymentType;
import com.TheAccountant.service.exception.ServiceException;

/**
 * Created by Florin on 7/31/2017.
 */
public interface PaymentService {

    /**
     * Method used to charge the session user with the amount specified into the DTO
     *
     * @param chargeDTO: the charge details for the logged user
     * @param paymentType: an enum with the type of payment that is transacted
     * @return: the details of the charge if the trasaction was successful
     * @throws ServiceException: exception thrown if en error event occurs while charging the user
     */
    ChargeDTO charge(ChargeDTO chargeDTO, PaymentType paymentType) throws ServiceException;

    /**
     * Check if the payment having the specified type was approved for the session user
     *
     * @param paymentType: an enum with the type of payment to get status
     * @return: the details of the charge including the status for the referred payment type
     * @throws ServiceException: exception thrown if en error event occurs while getting payment status for the user
     */
    ChargeDTO getPaymentStatus(PaymentType paymentType) throws ServiceException;
}
