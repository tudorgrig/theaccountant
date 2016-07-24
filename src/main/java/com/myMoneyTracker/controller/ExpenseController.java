package com.myMoneyTracker.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

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

import com.myMoneyTracker.controller.exception.BadRequestException;
import com.myMoneyTracker.controller.exception.NotFoundException;
import com.myMoneyTracker.converter.ExpenseConverter;
import com.myMoneyTracker.dao.CategoryDao;
import com.myMoneyTracker.dao.ExpenseDao;
import com.myMoneyTracker.dto.expense.ExpenseDTO;
import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.CurrencyUtil;
import com.myMoneyTracker.util.UserUtil;

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
    private CategoryDao categoryDao;
    
    @Autowired
    private ExpenseConverter expenseConverter;
    
    @Autowired
    private UserUtil userUtil;
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<ExpenseDTO> createExpense(@RequestBody @Valid Expense expense) {
    
        try {
            if(CurrencyUtil.getCurrency(expense.getCurrency())==null){
                throw new BadRequestException("Wrong currency code!");
            }
            String categoryName = expense.getCategory().getName();
            AppUser user = userUtil.extractLoggedAppUserFromDatabase();
            Category category = categoryDao.findByNameAndUsername(categoryName, user.getUsername());
            if (category == null) {
                category = createAndSaveCategory(categoryName, user);
            }
            expense.setCategory(category);
            expense.setUser(user);
            Expense createdExpense = expenseDao.saveAndFlush(expense);
            return new ResponseEntity<ExpenseDTO>(expenseConverter.convertTo(createdExpense), HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
    
    @RequestMapping(value = "/find_all", method = RequestMethod.GET)
    public ResponseEntity<List<ExpenseDTO>> listAllExpenses() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        List<Expense> expenses = expenseDao.findByUsername(user.getUsername());
        if (expenses.isEmpty()) {
            return new ResponseEntity<List<ExpenseDTO>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<ExpenseDTO>>(createExpenseDTOs(expenses), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/find/category/{category_name:.+}", method = RequestMethod.GET)
    public ResponseEntity<List<ExpenseDTO>> listAllExpensesByCategoryName(@PathVariable("category_name") String categoryName) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        List<Expense> expenses = expenseDao.findByCategoryNameAndUsername(categoryName, user.getUsername());
        if (expenses.isEmpty()) {
            return new ResponseEntity<List<ExpenseDTO>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<ExpenseDTO>>(createExpenseDTOs(expenses), HttpStatus.OK);
    }

    @RequestMapping(value = "/find/{category_name:.+}/{currency}/{start_time_millis}/{end_time_millis}", method = RequestMethod.GET)
    public ResponseEntity<List<ExpenseDTO>> listAllExpensesByCategoryNameAndTimeInterval(
            @PathVariable("category_name") String categoryName,
            @PathVariable("currency") String currency,
            @PathVariable("start_time_millis") long startTimeMillis,
            @PathVariable("end_time_millis") long endTimeMillis) {

        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        List<Expense> expenses = null;
        if (categoryName.equals("*")) {
            expenses = expenseDao.findByTimeInterval(user.getUsername(), new Timestamp(startTimeMillis),
                    new Timestamp(endTimeMillis));
        } else {
            expenses = expenseDao.findByTimeIntervalAndCategory(user.getUsername(), categoryName,
                    new Timestamp(startTimeMillis), new Timestamp(endTimeMillis));
        }
        if (expenses.isEmpty()) {
            return new ResponseEntity<List<ExpenseDTO>>(HttpStatus.NO_CONTENT);
        }
        convertExpenseCurrencies(expenses, currency);
        return new ResponseEntity<List<ExpenseDTO>>(createExpenseDTOs(expenses), HttpStatus.OK);
    }

    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<ExpenseDTO> findExpense(@PathVariable("id") Long id) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        Expense expense = expenseDao.findOne(id);
        if (expense == null) {
            throw new NotFoundException("Expense not found");
        }
        if (!(user.getUsername().equals(expense.getUser().getUsername()))) {
            throw new BadRequestException("Unauthorized access");
        }
        return new ResponseEntity<ExpenseDTO>(expenseConverter.convertTo(expense), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> updateExpense(@PathVariable("id") Long id, @RequestBody @Valid Expense expense) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        Expense oldExpense = expenseDao.findOne(id);
        if (oldExpense == null) {
            throw new NotFoundException("Expense not found");
        }
        if (!(user.getUsername().equals(oldExpense.getUser().getUsername()))) {
            throw new BadRequestException("Unauthorized access");
        }
        Category oldCategory = oldExpense.getCategory();
        String newExpenseCategoryName = expense.getCategory().getName();
        if (!oldCategory.getName().equals(newExpenseCategoryName)) {
            Category category = categoryDao.findByNameAndUsername(newExpenseCategoryName, user.getUsername());
            if (category == null) {
                category = createAndSaveCategory(newExpenseCategoryName, oldExpense.getUser());
            }
            expense.setCategory(category);
        }
        if(CurrencyUtil.getCurrency(expense.getCurrency()) == null){
            throw new BadRequestException("Wrong currency code");
        }
        expense.setId(id);
        expense.setUser(oldExpense.getUser());
        expenseDao.saveAndFlush(expense);
        return new ResponseEntity<String>("Expense updated", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteExpense(@PathVariable("id") Long id) {
        try {
            AppUser user = userUtil.extractLoggedAppUserFromDatabase();
            Expense expenseToBeDeleted = expenseDao.findOne(id);
            if (expenseToBeDeleted == null) {
                throw new EmptyResultDataAccessException("Expense not found", 1);
            }
            if (!(user.getUsername().equals(expenseToBeDeleted.getUser().getUsername()))) {
                return new ResponseEntity<String>("Unauthorized request", HttpStatus.BAD_REQUEST);
            }
            expenseDao.delete(id);
            expenseDao.flush();
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new NotFoundException("Expense not found");
        }
        return new ResponseEntity<String>("Expense deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete_all", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteAll() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        expenseDao.deleteAllByUsername(user.getUsername());
        expenseDao.flush();
        return new ResponseEntity<String>("Expenses deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete_all/{category_name:.+}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteAllByCategoryName(@PathVariable("category_name") String categoryName) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        expenseDao.deleteAllByCategoryNameAndUsername(categoryName, user.getUsername());
        expenseDao.flush();
        return new ResponseEntity<String>("Expenses deleted", HttpStatus.NO_CONTENT);
    }

    private void convertExpenseCurrencies(List<Expense> expenses, String currency) {
        expenses.stream().filter(expense -> !expense.getCurrency().equals(currency)).forEach(expense -> {
            try {
                float convertedAmount = YahooCurrencyConverter.convert(expense.getCurrency(), currency, expense.getAmount().floatValue());
                expense.setAmount(Double.valueOf(Float.toString(convertedAmount)));
            } catch (IOException e) {
                   throw new BadRequestException(e);
            }
            expense.setCurrency(currency);
        });
    }

    private Category createAndSaveCategory(String categoryName, AppUser user) {
    
        Category category = new Category();
        category.setName(categoryName);
        category.setUser(user);
        category = categoryDao.saveAndFlush(category);
        return category;
    }
    
    private List<ExpenseDTO> createExpenseDTOs(List<Expense> expenses) {
    
        List<ExpenseDTO> expenseDTOs = new ArrayList<ExpenseDTO>();
        expenses.stream().forEach(expense -> {
            expenseDTOs.add(expenseConverter.convertTo(expense));
        });
        return expenseDTOs;
    }
}
