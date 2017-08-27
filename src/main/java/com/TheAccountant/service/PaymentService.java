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
     * @param chargeDTO
     * @return
     * @throws ServiceException
     */
    ChargeDTO charge(ChargeDTO chargeDTO, PaymentType paymentType) throws ServiceException;
}
