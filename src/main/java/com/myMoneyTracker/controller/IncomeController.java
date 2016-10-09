package com.myMoneyTracker.controller;

import com.myMoneyTracker.controller.exception.BadRequestException;
import com.myMoneyTracker.converter.IncomeConverter;
import com.myMoneyTracker.dao.IncomeDao;
import com.myMoneyTracker.dto.income.IncomeDTO;
import com.myMoneyTracker.model.income.Income;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.CurrencyConverter;
import com.myMoneyTracker.util.CurrencyUtil;
import com.myMoneyTracker.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tudor
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

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;
    private static final Logger log = Logger.getLogger(AppUserController.class.getName());
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> createIncome(@RequestBody @Valid Income income) {
    
        try {
            if(CurrencyUtil.getCurrency(income.getCurrency()) == null){
                return new ResponseEntity<>("Wrong currency code!", HttpStatus.BAD_REQUEST);
            }
            AppUser user = userUtil.extractLoggedAppUserFromDatabase();
            income.setUser(user);
            if(!user.getDefaultCurrency().getCurrencyCode().equals(income.getCurrency())){
                setDefaultCurrencyAmount(income, user.getDefaultCurrency());
            }
            Income createdIncome = incomeDao.saveAndFlush(income);
            return new ResponseEntity<>(incomeConverter.convertTo(createdIncome), HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            log.log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //TODO: AGAIN, WHY?
    @RequestMapping(value = "/find_all", method = RequestMethod.GET)
    public ResponseEntity<List<IncomeDTO>> listAllIncomes() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        List<Income> incomes = incomeDao.findByUsername(user.getUsername());
        if (incomes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(createIncomeDTOs(incomes), HttpStatus.OK);
    }

    //TODO: WHY?
    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> findIncome(@PathVariable("id") Long id) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        Income income = incomeDao.findOne(id);
        if (income == null) {
            return new ResponseEntity<>("Income not found", HttpStatus.NOT_FOUND);
        }
        if (!(user.getUsername().equals(income.getUser().getUsername()))) {
            return new ResponseEntity<>("Unauthorized request", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(incomeConverter.convertTo(income), HttpStatus.OK);
    }


    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> updateIncome(@PathVariable("id") Long id, @RequestBody @Valid Income income) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        Income oldIncome = incomeDao.findOne(id);
        if (oldIncome == null) {
            return new ResponseEntity<>("Income not found", HttpStatus.NOT_FOUND);
        }
        if (!(user.getUsername().equals(oldIncome.getUser().getUsername()))) {
            return new ResponseEntity<>("Unauthorized request", HttpStatus.BAD_REQUEST);
        }
        if(CurrencyUtil.getCurrency(income.getCurrency()) == null){
            return new ResponseEntity<>("Wrong currency code!", HttpStatus.BAD_REQUEST);
        }
        income.setId(id);
        income.setUser(oldIncome.getUser());
        if(shouldUpdateDefaultCurrencyAmount(income, user, oldIncome)){
            setDefaultCurrencyAmount(income,user.getDefaultCurrency());
        }
        incomeDao.saveAndFlush(income);
        return new ResponseEntity<>("Income updated", HttpStatus.NO_CONTENT);
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
                return new ResponseEntity<>("Unauthorized request", HttpStatus.BAD_REQUEST);
            }
            incomeDao.delete(id);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            log.info(emptyResultDataAccessException.getMessage());
            return new ResponseEntity<>(emptyResultDataAccessException.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Income deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete_all", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAll() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        incomeDao.deleteAllByUsername(user.getUsername());
        incomeDao.flush();
        return new ResponseEntity<>("Incomes deleted", HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value ="/findByInterval/{startDate}/{endDate}", method = RequestMethod.GET)
    public ResponseEntity<?> findByInterval(@PathVariable("startDate") long startDate,
                                            @PathVariable("endDate") long endDate){
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        List<Income> incomes = incomeDao.findIncomesInTimeInterval(new Timestamp(startDate), new Timestamp(endDate), user.getUsername());
        if (incomes.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        convertIncomesToDefaultCurrency(incomes, user);
        return new ResponseEntity<>(createIncomeDTOs(incomes), HttpStatus.OK);
    }

    private List<IncomeDTO> createIncomeDTOs(List<Income> incomes) {
    
        List<IncomeDTO> incomeDTOs = new ArrayList<IncomeDTO>();
        incomes.stream().forEach(income -> {
            incomeDTOs.add(incomeConverter.convertTo(income));
        });
        return incomeDTOs;
    }


    private void setDefaultCurrencyAmount(Income income, Currency defaultCurrency){
        String incomeCurrency = income.getCurrency();
        Double amount = income.getAmount();
        String formatDate = new SimpleDateFormat("yyyy-MM-dd").format(income.getCreationDate().getTime());
        Double exchangeRateOnDay = null;
        try {
            exchangeRateOnDay = CurrencyConverter.getExchangeRateOnDay(incomeCurrency, defaultCurrency, formatDate);
            if(exchangeRateOnDay != null) {
                income.setDefaultCurrency(defaultCurrency.getCurrencyCode());
                income.setDefaultCurrencyAmount(amount * exchangeRateOnDay);
            }
        } catch (IOException e) {
            throw new BadRequestException(e);
        }
    }


    private boolean shouldUpdateDefaultCurrencyAmount(Income income, AppUser user, Income oldIncome) {
        boolean creationDateChanged = !income.getCreationDate().equals(oldIncome.getCreationDate())
                && (income.getCreationDate().getTime() - oldIncome.getCreationDate().getTime() >= ONE_DAY
                ||
                oldIncome.getCreationDate().getTime() - income.getCreationDate().getTime() >= ONE_DAY);

        boolean currencyChanged = !income.getCurrency().equals(oldIncome.getCurrency());
        boolean amountChanged = !income.getAmount().equals(oldIncome.getAmount());
        if(creationDateChanged || currencyChanged || amountChanged){
            //if any of those fields changed, check if the user updated the income with his own default currency.
            //if yes, no conversion is needed, if not conversion is needed between the income currency
            // and the users default currency
            boolean hasDiffCurrencyThanDefault = !income.getCurrency().equals(user.getDefaultCurrency().getCurrencyCode());
            if(!hasDiffCurrencyThanDefault){
                income.setDefaultCurrency(null);
                income.setDefaultCurrencyAmount(null);
            }
            return hasDiffCurrencyThanDefault;
        }
        return false;
    }

    private void convertIncomesToDefaultCurrency(List<Income> incomes, AppUser user) {
        incomes.stream().filter(income -> shouldUpdateDefaultCurrencyAmount(income, user)).forEach(income -> {
            setDefaultCurrencyAmount(income, user.getDefaultCurrency());
            incomeDao.saveAndFlush(income);
        });
    }

    private boolean shouldUpdateDefaultCurrencyAmount(Income income, AppUser user) {
        boolean incomeWasOnOldDefaultCurrency = income.getDefaultCurrency() == null &&
                !income.getCurrency().equals(user.getDefaultCurrency().getCurrencyCode());
        boolean userChangedDefaultCurrency = income.getDefaultCurrency() != null &&
                !income.getDefaultCurrency().equals(user.getDefaultCurrency().getCurrencyCode());
        return incomeWasOnOldDefaultCurrency || userChangedDefaultCurrency;
    }
}
