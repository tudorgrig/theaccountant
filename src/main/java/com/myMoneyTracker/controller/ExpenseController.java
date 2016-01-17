package com.myMoneyTracker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myMoneyTracker.converter.ExpenseConverter;
import com.myMoneyTracker.dao.AppUserDao;
import com.myMoneyTracker.dao.CategoryDao;
import com.myMoneyTracker.dao.ExpenseDao;
import com.myMoneyTracker.dto.expense.ExpenseDTO;
import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.ControllerUtil;

/**
 * REST controller for expense entity
 * 
 * @author Florin
 */
@RestController
@RequestMapping(value = "/expense")
public class ExpenseController {
    
    @Autowired
    private ExpenseDao expenseDao;
    
    @Autowired
    private AppUserDao appUserDao;
    
    @Autowired
    private CategoryDao categoryDao;
    
    @Autowired
    private ExpenseConverter expenseConverter;
    
    private static final Logger log = Logger.getLogger(AppUserController.class.getName());
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> createExpense(@RequestBody @Valid Expense expense) {
    
        try {
            String categoryName = expense.getCategory().getName();
            String loggedUsername = ControllerUtil.getCurrentLoggedUsername();
            AppUser user = appUserDao.findByUsername(loggedUsername);
            Category category = categoryDao.findByNameAndUsername(categoryName, loggedUsername);
            if (category == null) {
                Category categoryToBeInserted = new Category();
                categoryToBeInserted.setName(categoryName);
                categoryToBeInserted.setUser(user);
                category = categoryDao.saveAndFlush(categoryToBeInserted);
            }
            expense.setCategory(category);
            expense.setUser(user);
            Expense createdExpense = expenseDao.saveAndFlush(expense);
            return new ResponseEntity<ExpenseDTO>(expenseConverter.convertTo(createdExpense), HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            log.log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/find_all", method = RequestMethod.GET)
    public ResponseEntity<List<ExpenseDTO>> listAllExpenses() {
    
        List<Expense> expenses = expenseDao.findAll();
        if (expenses.isEmpty()) {
            return new ResponseEntity<List<ExpenseDTO>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<ExpenseDTO>>(createExpenseDTOs(expenses), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> findExpense(@PathVariable("id") Long id) {
    
        String loggedUsername = ControllerUtil.getCurrentLoggedUsername();
        Expense expense = expenseDao.findOne(id);
        if (expense == null || !(loggedUsername.equals(expense.getUser().getUsername()))) {
            return new ResponseEntity<String>("Expense not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ExpenseDTO>(expenseConverter.convertTo(expense), HttpStatus.OK);
    }
    
    private List<ExpenseDTO> createExpenseDTOs(List<Expense> expenses) {
        
        List<ExpenseDTO> expenseDTOs = new ArrayList<ExpenseDTO>();
        for (Expense expense : expenses) {
            expenseDTOs.add(expenseConverter.convertTo(expense));
        }
        return expenseDTOs;
    }
}
