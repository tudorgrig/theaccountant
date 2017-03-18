package com.TheAccountant.controller;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.controller.exception.ConflictException;
import com.TheAccountant.controller.exception.NotFoundException;
import com.TheAccountant.dao.LoanDao;
import com.TheAccountant.model.loan.Loan;
import com.TheAccountant.model.user.AppUser;
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

/**
 * Created by tudor.grigoriu on 3/17/2017.
 */
@RestController
@RequestMapping(value = "/loans")
public class LoanController {

    @Autowired
    LoanDao loanDao;

    @Autowired
    private UserUtil userUtil;

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Loan> create(@RequestBody @Valid Loan loan) {
        try {
            loan.setUser(userUtil.extractLoggedAppUserFromDatabase());
            Loan createdLoan = loanDao.saveAndFlush(loan);
            return new ResponseEntity<>(createdLoan, HttpStatus.OK);
        } catch (DataIntegrityViolationException dive) {
            throw new ConflictException(dive.getMostSpecificCause().getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<Loan>> findAll(@PathVariable("id") Long id) {
        AppUser appUser = userUtil.extractLoggedAppUserFromDatabase();
        if (appUser == null) {
            throw new NotFoundException("User not found");
        }
        return new ResponseEntity<>(loanDao.findByCounterparty(appUser.getUsername(), id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @Transactional
    public ResponseEntity<String> update(@PathVariable("id") Long id, @RequestBody @Valid Loan loan) {

        Loan oldLoan = loanDao.findOne(id);
        if (oldLoan == null) {
            throw new NotFoundException("Loan not found");
        }
        AppUser appUser = userUtil.extractLoggedAppUserFromDatabase();
        if(appUser.getUserId() != oldLoan.getUser().getUserId()){
            throw new BadRequestException("Bad request");
        }
        loan.setUser(appUser);
        loan.setId(id);
        loanDao.saveAndFlush(loan);
        return new ResponseEntity<>("Loan updated", HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {

        try {
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
        }
        return new ResponseEntity<>("Loan deleted", HttpStatus.NO_CONTENT);
    }
}
