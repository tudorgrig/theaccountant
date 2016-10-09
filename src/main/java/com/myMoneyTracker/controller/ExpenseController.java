package com.myMoneyTracker.controller;

import com.myMoneyTracker.controller.exception.BadRequestException;
import com.myMoneyTracker.controller.exception.NotFoundException;
import com.myMoneyTracker.converter.ExpenseConverter;
import com.myMoneyTracker.dao.CategoryDao;
import com.myMoneyTracker.dao.ExpenseDao;
import com.myMoneyTracker.dto.expense.ExpenseDTO;
import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.CurrencyConverter;
import com.myMoneyTracker.util.CurrencyUtil;
import com.myMoneyTracker.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * REST controller for expense entity
 * 
 * @author Florin
 */
@RestController
@RequestMapping(value = "/expense")
public class ExpenseController {

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;
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
            if(!user.getDefaultCurrency().getCurrencyCode().equals(expense.getCurrency())){
                setDefaultCurrencyAmount(expense, user.getDefaultCurrency());
            }
            expense.setCategory(category);
            expense.setUser(user);
            Expense createdExpense = expenseDao.saveAndFlush(expense);
            return new ResponseEntity<>(expenseConverter.convertTo(createdExpense), HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    //TODO: Why do we have this? If we implement list all expenses it should be based on time interval
    @RequestMapping(value = "/find_all", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<List<ExpenseDTO>> listAllExpenses() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        Set<Expense> expenses = expenseDao.findByUsername(user.getUsername());
        if (expenses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(createExpenseDTOs(expenses), HttpStatus.OK);
    }

    //TODO: SAME AS UPPER TODO
    @RequestMapping(value = "/find/category/{category_name:.+}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<List<ExpenseDTO>> listAllExpensesByCategoryName(@PathVariable("category_name") String categoryName) {

        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        Optional<Category> found =
                user.getCategories().stream().filter(category -> category.getName().equals(categoryName)).findFirst();
        if (!found.isPresent()) {
            throw new NotFoundException("Category not found");
        }
        Category category = found.get();
        if (category.getExpenses().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(createExpenseDTOs(category.getExpenses()), HttpStatus.OK);
    }

    @RequestMapping(value = "/find/{category_name:.+}/{start_time_millis}/{end_time_millis}", method = RequestMethod.GET)
    public ResponseEntity<List<ExpenseDTO>> listAllExpensesByCategoryNameAndTimeInterval(
            @PathVariable("category_name") String categoryName,
            @PathVariable("start_time_millis") long startTimeMillis,
            @PathVariable("end_time_millis") long endTimeMillis) {

        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        Set<Expense> expenses;
        if (categoryName.equals("*")) {
            expenses = expenseDao.findByTimeInterval(user.getUsername(), new Timestamp(startTimeMillis),
                    new Timestamp(endTimeMillis));
        } else {
            expenses = expenseDao.findByTimeIntervalAndCategory(user.getUsername(), categoryName,
                    new Timestamp(startTimeMillis), new Timestamp(endTimeMillis));
        }
        if (expenses.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        convertExpensesToDefaultCurrency(expenses, user);
        return new ResponseEntity(createExpenseDTOs(expenses), HttpStatus.OK);
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
        return new ResponseEntity<>(expenseConverter.convertTo(expense), HttpStatus.OK);
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
        if(shouldUpdateDefaultCurrencyAmount(expense, user, oldExpense)){
            setDefaultCurrencyAmount(expense,user.getDefaultCurrency());
        }
        expenseDao.saveAndFlush(expense);
        return new ResponseEntity<>("Expense updated", HttpStatus.NO_CONTENT);
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
                return new ResponseEntity<>("Unauthorized request", HttpStatus.BAD_REQUEST);
            }
            expenseDao.delete(id);
            expenseDao.flush();
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new NotFoundException("Expense not found");
        }
        return new ResponseEntity<>("Expense deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete_all", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteAll() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        expenseDao.deleteAllByUsername(user.getUsername());
        expenseDao.flush();
        return new ResponseEntity<>("Expenses deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete_all/{category_name:.+}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteAllByCategoryName(@PathVariable("category_name") String categoryName) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        expenseDao.deleteAllByCategoryNameAndUsername(categoryName, user.getUsername());
        expenseDao.flush();
        return new ResponseEntity<>("Expenses deleted", HttpStatus.NO_CONTENT);
    }

    private Category createAndSaveCategory(String categoryName, AppUser user) {
    
        Category category = new Category();
        category.setName(categoryName);
        category.setUser(user);
        category = categoryDao.saveAndFlush(category);
        return category;
    }
    
    private List<ExpenseDTO> createExpenseDTOs(Set<Expense> expenses) {
    
        List<ExpenseDTO> expenseDTOs = new ArrayList<ExpenseDTO>();
        expenses.stream().forEach(expense -> {
            expenseDTOs.add(expenseConverter.convertTo(expense));
        });
        return expenseDTOs;
    }

    private boolean shouldUpdateDefaultCurrencyAmount(Expense expense, AppUser user, Expense oldExpense) {
        boolean creationDateChanged = !expense.getCreationDate().equals(oldExpense.getCreationDate()) 
                && (expense.getCreationDate().getTime() - oldExpense.getCreationDate().getTime() >= ONE_DAY
                        ||
                    oldExpense.getCreationDate().getTime() - expense.getCreationDate().getTime() >= ONE_DAY);
                
        boolean currencyChanged = !expense.getCurrency().equals(oldExpense.getCurrency());
        boolean amountChanged = !expense.getAmount().equals(oldExpense.getAmount());
        if(creationDateChanged || currencyChanged || amountChanged){
            //if any of those fields changed, check if the user updated the expense with his own default currency.
            //if yes, no conversion is needed, if not conversion is needed between the expense currency
            // and the users default currency
            boolean hasDiffCurrencyThanDefault = !expense.getCurrency().equals(user.getDefaultCurrency().getCurrencyCode());
            if(!hasDiffCurrencyThanDefault){
                expense.setDefaultCurrency(null);
                expense.setDefaultCurrencyAmount(null);
            }
            return hasDiffCurrencyThanDefault;
        }
        return false;
    }

    private boolean shouldUpdateDefaultCurrencyAmount(Expense expense, AppUser user) {
        boolean expenseWasOnOldDefaultCurrency = expense.getDefaultCurrency() == null &&
                !expense.getCurrency().equals(user.getDefaultCurrency().getCurrencyCode());
        boolean userChangedDefaultCurrency = expense.getDefaultCurrency() != null &&
                !expense.getDefaultCurrency().equals(user.getDefaultCurrency().getCurrencyCode());
        return expenseWasOnOldDefaultCurrency || userChangedDefaultCurrency;
    }

    private void setDefaultCurrencyAmount(Expense expense, Currency defaultCurrency){
        String expenseCurrency = expense.getCurrency();
        Double amount = expense.getAmount();
        String formatDate = new SimpleDateFormat("yyyy-MM-dd").format(expense.getCreationDate().getTime());
        Double exchangeRateOnDay = null;
        try {
            exchangeRateOnDay = CurrencyConverter.getExchangeRateOnDay(expenseCurrency, defaultCurrency, formatDate);
            if(exchangeRateOnDay != null) {
                expense.setDefaultCurrency(defaultCurrency.getCurrencyCode());
                expense.setDefaultCurrencyAmount(amount * exchangeRateOnDay);
            }
        } catch (IOException e) {
            throw new BadRequestException(e);
        }
    }

    private void convertExpensesToDefaultCurrency(Set<Expense> expenses, AppUser user) {
        expenses.stream().filter(expense -> shouldUpdateDefaultCurrencyAmount(expense, user)).forEach(expense -> {
            setDefaultCurrencyAmount(expense, user.getDefaultCurrency());
            expenseDao.saveAndFlush(expense);
        });
    }



}
