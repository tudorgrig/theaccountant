package com.TheAccountant.controller;

import com.TheAccountant.controller.abstracts.CurrencyHolderController;
import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.controller.exception.ConflictException;
import com.TheAccountant.controller.exception.NotFoundException;
import com.TheAccountant.converter.LoanConverter;
import com.TheAccountant.dao.CounterpartyDao;
import com.TheAccountant.dao.LoanDao;
import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.dto.loan.LoanDTO;
import com.TheAccountant.model.loan.Loan;
import com.TheAccountant.model.payment.PaymentType;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.service.PaymentService;
import com.TheAccountant.service.exception.ServiceException;
import com.TheAccountant.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * Created by tudor.grigoriu on 3/17/2017.
 */
@RestController
@RequestMapping(value = "/loans")
public class LoanController extends CurrencyHolderController {

    @Autowired
    LoanDao loanDao;

    @Autowired
    CounterpartyDao counterpartyDao;

    @Autowired
    LoanConverter loanConverter;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<LoanDTO> create(@RequestBody @Valid Loan loan) {
        try {
            ChargeDTO chargeResult = paymentService.getPaymentStatus(PaymentType.USER_LICENSE);
            if (chargeResult.getPaymentApproved() == false) {
                throw new BadRequestException("Adding loans is available only to paid accounts!");
            }

            loan.setActive(true);
            AppUser loggedUser = userUtil.extractLoggedAppUserFromDatabase();
            if(loan.getCounterparty().getId() == 0){
                loan.getCounterparty().setUser(loggedUser);
                counterpartyDao.saveAndFlush(loan.getCounterparty());
            }
            loan.setUser(loggedUser);
            if (!loggedUser.getDefaultCurrency().getCurrencyCode().equals(loan.getCurrency())) {
                setDefaultCurrencyAmount(loan, loggedUser.getDefaultCurrency());
            }
            Loan createdLoan = loanDao.saveAndFlush(loan);
            return new ResponseEntity<>(loanConverter.convertTo(createdLoan), HttpStatus.OK);
        } catch (DataIntegrityViolationException dive) {
            throw new ConflictException(dive.getMostSpecificCause().getMessage());
        } catch (ServiceException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{counterpartyId}", method = RequestMethod.GET)
    public ResponseEntity<List<LoanDTO>> findAll(@PathVariable("counterpartyId") Long counterpartyId) {
        AppUser appUser = userUtil.extractLoggedAppUserFromDatabase();
        if (appUser == null) {
            throw new NotFoundException("User not found");
        }
        List<Loan> loans = loanDao.findByCounterparty(appUser.getUsername(), counterpartyId);
        if (loans == null || loans.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        convertLoansToDefaultCurrency(loans, appUser);
        return new ResponseEntity<>(loanConverter.convertToList(loans), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<LoanDTO>> findAll() {
        AppUser appUser = userUtil.extractLoggedAppUserFromDatabase();
        if (appUser == null) {
            throw new NotFoundException("User not found");
        }
        List<Loan> loans = loanDao.fetchAll(appUser.getUsername());
        if (loans == null || loans.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        convertLoansToDefaultCurrency(loans, appUser);
        return new ResponseEntity<>(loanConverter.convertToList(loans), HttpStatus.OK);
    }

    @RequestMapping(value = "/findOne/{id}", method = RequestMethod.GET)
    public ResponseEntity<LoanDTO> findOne(@PathVariable("id") Long id) {
        AppUser appUser = userUtil.extractLoggedAppUserFromDatabase();
        if (appUser == null) {
            throw new NotFoundException("User not found");
        }
        Loan loan = loanDao.findOne(id);
        if (loan == null || loan.getUser().getUserId() != appUser.getUserId()) {
            throw  new BadRequestException("Invalid Loan id [" + id + "]");
        }
        return new ResponseEntity<>(loanConverter.convertTo(loan), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @Transactional
    public ResponseEntity<String> update(@PathVariable("id") Long id, @RequestBody @Valid Loan loan) {

        ChargeDTO chargeResult = null;
        try {
            chargeResult = paymentService.getPaymentStatus(PaymentType.USER_LICENSE);
        } catch (ServiceException e) {
            throw new BadRequestException(e.getMessage());
        }
        if (chargeResult.getPaymentApproved() == false) {
            throw new BadRequestException("Updating loans is available only to paid accounts!");
        }

        Loan oldLoan = loanDao.findOne(id);
        if (oldLoan == null) {
            throw new NotFoundException("Loan not found");
        }
        AppUser appUser = userUtil.extractLoggedAppUserFromDatabase();
        if(appUser.getUserId() != oldLoan.getUser().getUserId()){
            throw new BadRequestException("Bad request");
        }
        if(loan.getCounterparty().getId() == 0){
            counterpartyDao.saveAndFlush(loan.getCounterparty());
        }
        loan.setUser(appUser);
        if(shouldUpdateDefaultCurrencyAmount(loan, appUser, oldLoan)){
            setDefaultCurrencyAmount(loan, appUser.getDefaultCurrency());
        }
        loan.setId(id);
        loanDao.saveAndFlush(loan);
        return new ResponseEntity<>("Loan updated", HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {

        try {
            ChargeDTO chargeResult = paymentService.getPaymentStatus(PaymentType.USER_LICENSE);
            if (chargeResult.getPaymentApproved() == false) {
                throw new BadRequestException("Deleting loans is available only to paid accounts!");
            }

            userUtil.extractLoggedAppUserFromDatabase();
            Loan loan = loanDao.findOne(id);
            if(loan == null){
                throw new NotFoundException("Loan not found");
            }
            AppUser appUser = userUtil.extractLoggedAppUserFromDatabase();
            if(appUser.getUserId() != loan.getUser().getUserId()){
                throw new BadRequestException("Bad request");
            }
            loanDao.delete(id);
            loanDao.flush();
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new NotFoundException(emptyResultDataAccessException.getMessage());
        } catch (ServiceException e) {
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>("Loan deleted", HttpStatus.NO_CONTENT);
    }

    private void convertLoansToDefaultCurrency(List<Loan> loans, AppUser user) {
        loans.stream().filter(loan -> shouldUpdateDefaultCurrencyAmount(loan, user)).forEach(loan -> {
            setDefaultCurrencyAmount(loan, user.getDefaultCurrency());
            loanDao.saveAndFlush(loan);
        });
    }
}
