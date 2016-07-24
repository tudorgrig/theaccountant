package com.myMoneyTracker.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import com.myMoneyTracker.controller.exception.BadRequestException;
import com.myMoneyTracker.util.YahooCurrencyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myMoneyTracker.converter.IncomeConverter;
import com.myMoneyTracker.dao.IncomeDao;
import com.myMoneyTracker.dto.income.IncomeDTO;
import com.myMoneyTracker.model.income.Income;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.CurrencyUtil;
import com.myMoneyTracker.util.UserUtil;

/**
 * @author Floryn
 * REST controller for income entity
 *
 */
@RestController
@RequestMapping(value = "/income")
public class IncomeController {
    
    @Autowired
    IncomeDao incomeDao;
    
    @Autowired
    private UserUtil userUtil;
    
    @Autowired
    IncomeConverter incomeConverter;
    
    private static final Logger log = Logger.getLogger(AppUserController.class.getName());
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> createIncome(@RequestBody @Valid Income income) {
    
        try {
            if(CurrencyUtil.getCurrency(income.getCurrency()) == null){
                return new ResponseEntity<String>("Wrong currency code!", HttpStatus.BAD_REQUEST);
            }
            AppUser user = userUtil.extractLoggedAppUserFromDatabase();
            income.setUser(user);
            Income createdIncome = incomeDao.saveAndFlush(income);
            return new ResponseEntity<IncomeDTO>(incomeConverter.convertTo(createdIncome), HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            log.log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/find_all", method = RequestMethod.GET)
    public ResponseEntity<List<IncomeDTO>> listAllIncomes() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        List<Income> incomes = incomeDao.findByUsername(user.getUsername());
        if (incomes.isEmpty()) {
            return new ResponseEntity<List<IncomeDTO>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<IncomeDTO>>(createIncomeDTOs(incomes), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> findIncome(@PathVariable("id") Long id) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        Income income = incomeDao.findOne(id);
        if (income == null) {
            return new ResponseEntity<String>("Income not found", HttpStatus.NOT_FOUND);
        }
        if (!(user.getUsername().equals(income.getUser().getUsername()))) {
            return new ResponseEntity<String>("Unauthorized request", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<IncomeDTO>(incomeConverter.convertTo(income), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> updateIncome(@PathVariable("id") Long id, @RequestBody @Valid Income income) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        Income oldIncome = incomeDao.findOne(id);
        if (oldIncome == null) {
            return new ResponseEntity<String>("Income not found", HttpStatus.NOT_FOUND);
        }
        if (!(user.getUsername().equals(oldIncome.getUser().getUsername()))) {
            return new ResponseEntity<String>("Unauthorized request", HttpStatus.BAD_REQUEST);
        }
        if(CurrencyUtil.getCurrency(income.getCurrency()) == null){
            return new ResponseEntity<String>("Wrong currency code!", HttpStatus.BAD_REQUEST);
        }
        income.setId(id);
        income.setUser(oldIncome.getUser());
        incomeDao.saveAndFlush(income);
        return new ResponseEntity<String>("Income updated", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteIncome(@PathVariable("id") Long id) {
    
        try {
            AppUser user = userUtil.extractLoggedAppUserFromDatabase();
            Income incomeToBeDeleted = incomeDao.findOne(id);
            if (incomeToBeDeleted == null) {
                throw new EmptyResultDataAccessException("Income not found", 1);
            }
            if (!(user.getUsername().equals(incomeToBeDeleted.getUser().getUsername()))) {
                return new ResponseEntity<String>("Unauthorized request", HttpStatus.BAD_REQUEST);
            }
            incomeDao.delete(id);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            log.info(emptyResultDataAccessException.getMessage());
            return new ResponseEntity<String>(emptyResultDataAccessException.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>("Income deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete_all", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAll() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        incomeDao.deleteAllByUsername(user.getUsername());
        incomeDao.flush();
        return new ResponseEntity<String>("Incomes deleted", HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value ="/findByInterval/{startDate}/{endDate}/{currency}", method = RequestMethod.GET)
    public ResponseEntity<?> findByInterval(@PathVariable("startDate") long startDate,
                                            @PathVariable("endDate") long endDate,
                                            @PathVariable("currency") String currency){
        if(CurrencyUtil.getCurrency(currency) == null){
            return new ResponseEntity<String>("Wrong currency code!", HttpStatus.BAD_REQUEST);
        }
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        List<Income> incomes = incomeDao.findIncomesInTimeInterval(new Timestamp(startDate), new Timestamp(endDate), user.getUsername());
        convertCurrencies(incomes, currency);
        return new ResponseEntity<List<IncomeDTO>>(createIncomeDTOs(incomes), HttpStatus.OK);
    }

    private void convertCurrencies(List<Income> incomes, String currency) {
        incomes.stream().filter(income -> !income.getCurrency().equals(currency)).forEach(income -> {
            try {
                float convertedAmount = YahooCurrencyConverter.convert(income.getCurrency(), currency, income.getAmount().floatValue());
                income.setAmount(Double.valueOf(Float.toString(convertedAmount)));
            } catch (IOException e) {
                throw new BadRequestException(e);
            }
            income.setCurrency(currency);
        });
    }

    private List<IncomeDTO> createIncomeDTOs(List<Income> incomes) {
    
        List<IncomeDTO> incomeDTOs = new ArrayList<IncomeDTO>();
        incomes.stream().forEach(income -> {
            incomeDTOs.add(incomeConverter.convertTo(income));
        });
        return incomeDTOs;
    }
}
