package com.myMoneyTracker.converter;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.myMoneyTracker.dto.income.IncomeDTO;
import com.myMoneyTracker.model.income.Income;
import com.myMoneyTracker.model.user.AppUser;

/**
 * Test class for IncomeConverter dozer mapping class
 * @author Floryn
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
public class IncomeConverterTest {
    
    @Autowired
    IncomeConverter incomeConverter;
    
    @Test
    public void shouldConvertIncomeToIncomeDTO() {
    
        AppUser appUser = createAppUser("my-money-tracker@gmail.com", "floryn");
        Income income = createIncome(appUser);
        IncomeDTO incomeDTO = incomeConverter.convertTo(income);
        assertEquals(income.getAmount(), incomeDTO.getAmount());
        assertEquals(income.getId(), incomeDTO.getId());
        assertEquals(income.getName(), incomeDTO.getName());
        assertEquals(income.getCreationDate(), incomeDTO.getCreationDate());
        assertEquals(income.getDescription(), incomeDTO.getDescription());
    }
    
    @Test
    public void shouldConvertIncomeDTOToIncome() {
    
        IncomeDTO incomeDTO = createIncomeDTO();
        Income income = incomeConverter.convertFrom(incomeDTO);
        assertEquals(incomeDTO.getAmount(), income.getAmount());
        assertEquals(incomeDTO.getId(), income.getId());
        assertEquals(incomeDTO.getName(), income.getName());
        assertEquals(incomeDTO.getCreationDate(), income.getCreationDate());
        assertEquals(incomeDTO.getDescription(), income.getDescription());
    }
    
    private IncomeDTO createIncomeDTO() {
    
        IncomeDTO income = new IncomeDTO();
        income.setName("name1");
        income.setDescription("description1");
        income.setAmount(new Double(222.222));
        income.setCreationDate(new Timestamp(System.currentTimeMillis()));
        return income;
    }
    
    private Income createIncome(AppUser appUser) {
    
        Income income = new Income();
        income.setName("name1");
        income.setDescription("description1");
        income.setAmount(new Double(222.222));
        income.setCreationDate(new Timestamp(System.currentTimeMillis()));
        income.setUser(appUser);
        return income;
    }
    
    private AppUser createAppUser(String email, String username) {
    
        AppUser appUser = new AppUser();
        appUser.setId(1);
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setUsername(username);
        appUser.setBirthdate(new Date());
        appUser.setEmail(email);
        return appUser;
    }
    
}
