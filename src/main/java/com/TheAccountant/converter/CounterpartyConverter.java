package com.TheAccountant.converter;

import com.TheAccountant.dto.counterparty.CounterpartyDTO;
import com.TheAccountant.model.counterparty.Counterparty;
import com.TheAccountant.model.loan.Loan;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

import java.util.Set;

/**
 * Created by tudor.grigoriu on 3/18/2017.
 */
public class CounterpartyConverter {

    public CounterpartyDTO convertTo(Counterparty counterparty) {
        CounterpartyDTO counterpartyDTO = new CounterpartyDTO();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(counterparty, counterpartyDTO);
        counterpartyDTO.setTotal(computeTotalAmount(counterparty.getLoans()));
        return counterpartyDTO;
    }

    private double computeTotalAmount(Set<Loan> loans) {
        return loans.stream()
                .filter(loan -> loan.getActive() == true)
                .mapToDouble(loan -> getAmount(loan)).sum();
    }

    private Double getAmount(Loan loan) {
        Double amount = new Double(0);
        if(loan.getActive() == Boolean.TRUE) {

            if (loan.getDefaultCurrencyAmount() != null) {
                amount = loan.getDefaultCurrencyAmount();
            } else {
                amount = loan.getAmount();
            }
            if (loan.getReceiving() == Boolean.FALSE) {
                //if user is giving, it means that the logged in user must give money to the counterparty
                return -1 * amount;
            }
        }
        return amount;
    }

    public Counterparty convertFrom(CounterpartyDTO counterpartyDTO) {
        Counterparty counterparty = new Counterparty();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(counterpartyDTO, counterparty);
        return counterparty;
    }
}
