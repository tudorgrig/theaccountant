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
@RequestMapping(value = "/payments")
public class PaymentController {

    private static final Logger LOG = Logger.getLogger(PaymentController.class.getName());

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<ChargeDTO> charge(@RequestBody @Valid ChargeDTO chargeDTO) {

        LOG.info(" Charge controller new chargeDTO: " + chargeDTO.getDescription());

        ChargeDTO resultDTO;
        try {
            resultDTO = paymentService.charge(chargeDTO, PaymentType.USER_LICENSE);
        } catch (ServiceException e) {
            LOG.log(Level.SEVERE, e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>(resultDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<ChargeDTO> getPaymentStatusForUser() {

        PaymentType paymentTypeEnum = PaymentType.USER_LICENSE;

        ChargeDTO resultDTO;
        try {
            resultDTO = paymentService.getPaymentStatus(paymentTypeEnum);
        } catch (ServiceException e) {
            LOG.log(Level.SEVERE, e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>(resultDTO, HttpStatus.OK);
    }
}