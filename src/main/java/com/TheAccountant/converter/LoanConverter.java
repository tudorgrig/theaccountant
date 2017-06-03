package com.TheAccountant.converter;

import com.TheAccountant.dto.loan.LoanDTO;
import com.TheAccountant.model.loan.Loan;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tudor.grigoriu on 6/3/2017.
 */
public class LoanConverter {

    public LoanDTO convertTo(Loan loan) {

        LoanDTO loanDTO = new LoanDTO();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(loan, loanDTO);
        return loanDTO;
    }

    public List<LoanDTO> convertToList(List<Loan> loans) {
        return loans.stream().map(loan -> convertTo(loan)).collect(Collectors.toList());
    }
}
