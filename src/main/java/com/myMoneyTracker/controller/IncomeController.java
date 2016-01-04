package com.myMoneyTracker.controller;

import com.myMoneyTracker.dao.IncomeDao;
import com.myMoneyTracker.model.income.Income;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Tudor Grigoriu
 * REST controller for income entity
 *
 */
@RestController
@RequestMapping(value = "/income")
public class IncomeController {

    @Autowired
    IncomeDao incomeDao;

    private static final Logger log = Logger.getLogger(AppUserController.class.getName());

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Income> createAppUser(@RequestBody @Valid Income income) {

        Income createdIncome = incomeDao.saveAndFlush(income);
        return new ResponseEntity<Income>(createdIncome, HttpStatus.OK);
    }

    @RequestMapping(value = "/find_all", method = RequestMethod.GET)
    public ResponseEntity<List<Income>> listAllIncomes() {

        List<Income> incomes = incomeDao.findAll();
        if (incomes.isEmpty()) {
            return new ResponseEntity<List<Income>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Income>>(incomes, HttpStatus.OK);
    }

    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> findIncome(@PathVariable("id") Long id) {

        Income income = incomeDao.findOne(id);
        if (income == null) {
            return new ResponseEntity<String>("Income not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Income>(income, HttpStatus.OK);
    }

    @RequestMapping(value = "/find/user/{user_id}", method = RequestMethod.GET)
    public ResponseEntity<?> findByUserId(@PathVariable("user_id") Long id) {

        List<Income> incomeList = incomeDao.findByUserId(id);
        if (incomeList == null || incomeList.isEmpty()) {
            return new ResponseEntity<String>("Income not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Income>>(incomeList, HttpStatus.OK);
    }

    @RequestMapping(value = "/find/category/{category_id}", method = RequestMethod.GET)
    public ResponseEntity<?> findByCategoryId(@PathVariable("category_id") Long id) {

        List<Income> incomeList = incomeDao.findByCategoryId(id);
        if (incomeList == null || incomeList.isEmpty()) {
            return new ResponseEntity<String>("Income not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Income>>(incomeList, HttpStatus.OK);
    }

    @RequestMapping(value = "/find/subcategory/{subcategory_id}", method = RequestMethod.GET)
    public ResponseEntity<?> findBySubcategoryId(@PathVariable("subcategory_id") Long id) {

        List<Income> incomeList = incomeDao.findBySubcategoryId(id);
        if (incomeList == null || incomeList.isEmpty()) {
            return new ResponseEntity<String>("Income not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Income>>(incomeList, HttpStatus.OK);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> updateIncome(@PathVariable("id") Long id, @RequestBody @Valid Income income) {

        Income oldIncome = incomeDao.findOne(id);
        if (oldIncome == null) {
            return new ResponseEntity<String>("Income not found", HttpStatus.NOT_FOUND);
        }
        income.setId(id);
        incomeDao.saveAndFlush(income);
        return new ResponseEntity<String>("Income updated", HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteIncome(@PathVariable("id") Long id) {

        try {
            incomeDao.delete(id);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            log.info(emptyResultDataAccessException.getMessage());
            return new ResponseEntity<String>(emptyResultDataAccessException.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>("Income deleted", HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/deleteAll/", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAll() {

        incomeDao.deleteAll();
        return new ResponseEntity<String>("Incomes deleted", HttpStatus.NO_CONTENT);
    }
}
