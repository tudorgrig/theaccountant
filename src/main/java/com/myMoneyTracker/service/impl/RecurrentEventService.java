package com.myMoneyTracker.service.impl;

import com.myMoneyTracker.dao.ExpenseDao;
import com.myMoneyTracker.dao.IncomeDao;
import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.income.Income;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.util.Calendar;
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
            expenseDao.saveAndFlush(clone);
        });
    }
}
