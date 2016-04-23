package com.myMoneyTracker.service.impl;

import com.myMoneyTracker.dao.ExpenseDao;
import com.myMoneyTracker.dao.IncomeDao;
import com.myMoneyTracker.dao.RecurrentExpenseEventDao;
import com.myMoneyTracker.dao.RecurrentIncomeEventDao;
import com.myMoneyTracker.model.expense.Expense;
import com.myMoneyTracker.model.expense.RecurrentExpenseEvent;
import com.myMoneyTracker.model.income.Income;
import com.myMoneyTracker.model.income.RecurrentIncomeEvent;
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
    private RecurrentIncomeEventDao recurrentIncomeEventDao;

    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private RecurrentExpenseEventDao recurrentExpenseEventDao;

    @Scheduled(cron = "0 0 0 * * *") //everyday at midnight and 10 seconds
    public void addRecurrentIncomeEvents(){
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1; //January is 0
        List<Income> recurrentIncomeEventList = recurrentIncomeEventDao.findRecurrentIncomesToAdd(day, month);
        recurrentIncomeEventList.parallelStream().forEach(income -> {
            income.setCreationDate(new Timestamp(new Date().getTime()));
            income.setId(0);
            incomeDao.saveAndFlush(income);
        });
    }

    @Scheduled(cron = "0 0 0 * * *") //everyday at midnight and 30 seconds
    public void addRecurrentExpenseEvents(){
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1; //January is 0
        List<Expense> recurrentExpensesToAdd = recurrentExpenseEventDao.findRecurrentExpensesToAdd(day, month);
        recurrentExpensesToAdd.parallelStream().forEach(expense -> {
            expense.setCreationDate(new Timestamp(new Date().getTime()));
            expense.setId(0);
            expenseDao.saveAndFlush(expense);
        });
    }
}
