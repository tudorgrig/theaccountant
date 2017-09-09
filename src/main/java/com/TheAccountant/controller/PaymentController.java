package com.TheAccountant.controller;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.dto.counterparty.CounterpartyDTO;
import com.TheAccountant.model.counterparty.Counterparty;
import com.TheAccountant.model.payment.PaymentType;
import com.TheAccountant.service.PaymentService;
import com.TheAccountant.service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/payment")
public class PaymentController {

    private static final Logger LOG = Logger.getLogger(AppUserController.class.getName());

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(value = "/user_license", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> charge(@RequestBody @Valid ChargeDTO chargeDTO) {

        LOG.info("------ Charge controller chargeDTO: " + chargeDTO.toString());

        ChargeDTO resultDTO;
        try {
            resultDTO = paymentService.charge(chargeDTO, PaymentType.USER_LICENSE);
        } catch (ServiceException e) {
            LOG.log(Level.SEVERE, e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>(resultDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/getPaymentStatusForUser/{paymentType}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> getPaymentStatusForUser(@PathVariable("paymentType") String paymentType) {

        PaymentType paymentTypeEnum;
        if (paymentType.equalsIgnoreCase(PaymentType.USER_LICENSE.name())) {
            paymentTypeEnum = PaymentType.USER_LICENSE;
        } else {
            LOG.log(Level.SEVERE, "Payment type '" + paymentType + "' not recognized!");
            throw new BadRequestException("Payment type '" + paymentType + "' not recognized!");
        }

        ChargeDTO resultDTO;
        try {
            resultDTO = paymentService.getPaymentStatusForUser(paymentTypeEnum);
        } catch (ServiceException e) {
            LOG.log(Level.SEVERE, e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>(resultDTO, HttpStatus.OK);
    }
}