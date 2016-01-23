package com.myMoneyTracker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

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
import com.myMoneyTracker.dao.AppUserDao;
import com.myMoneyTracker.dao.IncomeDao;
import com.myMoneyTracker.dto.income.IncomeDTO;
import com.myMoneyTracker.model.income.Income;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.ControllerUtil;

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
    private AppUserDao appUserDao;
    
    @Autowired
    IncomeConverter incomeConverter;
    
    private static final Logger log = Logger.getLogger(AppUserController.class.getName());
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> createIncome(@RequestBody @Valid Income income) {
    
        try {
            String loggedUsername = ControllerUtil.getCurrentLoggedUsername();
            AppUser user = appUserDao.findByUsername(loggedUsername);
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
    
        String loggedUsername = ControllerUtil.getCurrentLoggedUsername();
        List<Income> incomes = incomeDao.findByUsername(loggedUsername);
        if (incomes.isEmpty()) {
            return new ResponseEntity<List<IncomeDTO>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<IncomeDTO>>(createIncomeDTOs(incomes), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> findIncome(@PathVariable("id") Long id) {
    
        String loggedUsername = ControllerUtil.getCurrentLoggedUsername();
        Income income = incomeDao.findOne(id);
        if (income == null || !(loggedUsername.equals(income.getUser().getUsername()))) {
            return new ResponseEntity<String>("Income not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<IncomeDTO>(incomeConverter.convertTo(income), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> updateIncome(@PathVariable("id") Long id, @RequestBody @Valid Income income) {
    
        String loggedUsername = ControllerUtil.getCurrentLoggedUsername();
        Income oldIncome = incomeDao.findOne(id);
        if (oldIncome == null || !(loggedUsername.equals(oldIncome.getUser().getUsername()))) {
            return new ResponseEntity<String>("Income not found", HttpStatus.NOT_FOUND);
        }
        income.setId(id);
        income.setUser(oldIncome.getUser());
        incomeDao.saveAndFlush(income);
        return new ResponseEntity<String>("Income updated", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteIncome(@PathVariable("id") Long id) {
    
        try {
            String loggedUsername = ControllerUtil.getCurrentLoggedUsername();
            Income incomeToBeDeleted = incomeDao.findOne(id);
            if (incomeToBeDeleted == null || !(loggedUsername.equals(incomeToBeDeleted.getUser().getUsername()))) {
                throw new EmptyResultDataAccessException("Income not found", 1);
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
    
        String loggedUsername = ControllerUtil.getCurrentLoggedUsername();
        incomeDao.deleteAllByUsername(loggedUsername);
        incomeDao.flush();
        return new ResponseEntity<String>("Incomes deleted", HttpStatus.NO_CONTENT);
    }
    
    private List<IncomeDTO> createIncomeDTOs(List<Income> incomes) {
    
        List<IncomeDTO> incomeDTOs = new ArrayList<IncomeDTO>();
        for (Income income : incomes) {
            incomeDTOs.add(incomeConverter.convertTo(income));
        }
        return incomeDTOs;
    }
}
