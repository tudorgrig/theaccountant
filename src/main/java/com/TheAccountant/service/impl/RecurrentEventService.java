package com.TheAccountant.service.impl;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.dao.ExpenseDao;
import com.TheAccountant.dao.IncomeDao;
import com.TheAccountant.model.expense.Expense;
import com.TheAccountant.model.income.Income;
import com.TheAccountant.util.CurrencyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;

/**
 * Created by tudor.grigoriu on 23.04.2016.
 */
public class RecurrentEventService {

    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private ExpenseDao expenseDao;

    @Scheduled(cron = "0 0 0 * * *") //everyday at midnight
    public void addRecurrentIncomeEvents(){
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1; //January is 0
        List<Income> recurrentIncomeEventList = incomeDao.findRecurrentIncomesToAdd(day, month);
        recurrentIncomeEventList.stream().forEach(income -> {
            Income clone = income.clone();
            clone.setCreationDate(new Timestamp(new Date().getTime()));
            if(!clone.getCurrency().equals(clone.getUser().getDefaultCurrency().getCurrencyCode())){
                setDefaultCurrencyAmount(clone, clone.getUser().getDefaultCurrency());
            }
            incomeDao.saveAndFlush(clone);
        });
    }

    @Scheduled(cron = "0 0 0 * * *") //everyday at midnight
    public void addRecurrentExpenseEvents(){
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1; //January is 0
        List<Expense> recurrentExpensesToAdd = expenseDao.findRecurrentExpensesToAdd(day, month);
        recurrentExpensesToAdd.stream().forEach(expense -> {
            Expense clone = expense.clone();
            clone.setCreationDate(new Timestamp(new Date().getTime()));
            if(!clone.getCurrency().equals(clone.getUser().getDefaultCurrency().getCurrencyCode())){
                setDefaultCurrencyAmount(clone, clone.getUser().getDefaultCurrency());
            }
            expenseDao.saveAndFlush(clone);
        });
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
}
