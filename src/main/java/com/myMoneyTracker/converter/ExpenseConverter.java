package com.myMoneyTracker.converter;

import com.myMoneyTracker.dto.expense.ExpenseDTO;
import com.myMoneyTracker.model.expense.Expense;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

/**
 * Dozer converter between expense and expenseDTO
 * 
 * @author Florin
 */
public class ExpenseConverter {
    
    public ExpenseDTO convertTo(Expense expense) {
    
        ExpenseDTO expenseDTO = new ExpenseDTO();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(expense, expenseDTO);
        return expenseDTO;
    }
    
    public Expense convertFrom(ExpenseDTO expenseDTO) {
    
        Expense expense = new Expense();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(expenseDTO, expense);
        return expense;
    }
}
