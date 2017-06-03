package com.TheAccountant.controller;

import com.TheAccountant.controller.abstracts.CurrencyHolderController;
import com.TheAccountant.converter.IncomeConverter;
import com.TheAccountant.dao.IncomeDao;
import com.TheAccountant.dto.income.IncomeDTO;
import com.TheAccountant.model.income.Income;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.util.CurrencyUtil;
import com.TheAccountant.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.ArrayList;
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
public class IncomeController extends CurrencyHolderController {

    @Autowired
    IncomeDao incomeDao;

    @Autowired
    private UserUtil userUtil;
    
    @Autowired
    IncomeConverter incomeConverter;

    private static final Logger log = Logger.getLogger(AppUserController.class.getName());

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> createIncomes(@RequestBody @Valid Income[] incomes) {

        try {
            if (incomes == null || incomes.length == 0) {
                return new ResponseEntity<>("No incomes found in request!", HttpStatus.BAD_REQUEST);
            } else {
                List<IncomeDTO> createdIncomeListDTO = new ArrayList<>();
                int index = 0;
                for (Income income : incomes) {
                    if (CurrencyUtil.getCurrency(income.getCurrency()) == null) {
                        return new ResponseEntity<>("Wrong currency code for index [" + index + "] and Currency code [" + income.getCurrency() + "]!", HttpStatus.BAD_REQUEST);
                    }
                    AppUser user = userUtil.extractLoggedAppUserFromDatabase();
                    income.setUser(user);
                    if (!user.getDefaultCurrency().getCurrencyCode().equals(income.getCurrency())) {
                        setDefaultCurrencyAmount(income, user.getDefaultCurrency());
                    }
                    Income savedIncome = incomeDao.saveAndFlush(income);
                    IncomeDTO createdIncomeDTO = incomeConverter.convertTo(savedIncome);
                    createdIncomeListDTO.add(createdIncomeDTO);
                    index++;
                }
                return new ResponseEntity<>(createdIncomeListDTO, HttpStatus.OK);
            }

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


    protected void convertIncomesToDefaultCurrency(List<Income> entityList, AppUser user) {
        entityList.stream().filter(income -> shouldUpdateDefaultCurrencyAmount(income, user)).forEach(income -> {
            setDefaultCurrencyAmount(income, user.getDefaultCurrency());
            incomeDao.saveAndFlush(income);
        });
    }

}
