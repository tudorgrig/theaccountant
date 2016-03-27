package com.myMoneyTracker.converter;

import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

import com.myMoneyTracker.dto.income.IncomeDTO;
import com.myMoneyTracker.model.income.Income;

/**
 * Dozer converter between income and incomeDTO
 * @author Floryn
 */
public class IncomeConverter {

    public IncomeDTO convertTo(Income income) {

        IncomeDTO destObject = new IncomeDTO();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(income, destObject);
        return destObject;
    }

    public Income convertFrom(IncomeDTO incomeDTO) {

        Income destObject = new Income();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(incomeDTO, destObject);
        return destObject;
    }
}
