package com.TheAccountant.converter;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.TheAccountant.dto.category.CategoryDTO;
import com.TheAccountant.dto.expense.ExpenseDTO;
import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.expense.Expense;
import com.TheAccountant.model.user.AppUser;

/**
 * Test class for categoryConverter dozer mapping class
 * 
 * @author Florin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
public class ExpenseConverterTest {
    
    @Autowired
    ExpenseConverter expenseConverter;
    
    @Test
    public void shouldConvertExpenseToExpenseDTO() {
    
        AppUser appUser = createAppUser("my-money-tracker@gmail.com", "florin");
        Expense expense = createExpense(appUser);
        ExpenseDTO expenseDTO = expenseConverter.convertTo(expense);
        assertEquals(expense.getAmount(), expenseDTO.getAmount());
        assertEquals(expense.getId(), expenseDTO.getId());
        assertEquals(expense.getName(), expenseDTO.getName());
        assertEquals(expense.getCurrency(), expenseDTO.getCurrency());
        assertEquals(expense.getCreationDate(), expenseDTO.getCreationDate());
        assertEquals(expense.getDescription(), expenseDTO.getDescription());
    }
    
    @Test
    public void shouldConvertExpenseDTOToExpense() {
    
        ExpenseDTO expenseDTO = createExpenseDTO();
        Expense expense = expenseConverter.convertFrom(expenseDTO);
        assertEquals(expenseDTO.getAmount(), expense.getAmount());
        assertEquals(expenseDTO.getId(), expense.getId());
        assertEquals(expenseDTO.getName(), expense.getName());
        assertEquals(expenseDTO.getCreationDate(), expense.getCreationDate());
        assertEquals(expenseDTO.getCurrency(), expense.getCurrency());
        assertEquals(expenseDTO.getDescription(), expense.getDescription());
    }
    
    private Expense createExpense(AppUser appUser) {
    
        Expense expense = new Expense();
        expense.setCategory(createCategory(appUser));
        expense.setName("ExpenseName1");
        expense.setDescription("Description1");
        expense.setAmount(new Double(222.222));
        expense.setCurrency("USD");
        expense.setCreationDate(new Timestamp(System.currentTimeMillis()));
        expense.setUser(appUser);
        return expense;
    }
    
    private ExpenseDTO createExpenseDTO() {
        
        ExpenseDTO expense = new ExpenseDTO();
        expense.setName("ExpenseName1");
        expense.setCategory(createCategoryDTO());
        expense.setDescription("Description1");
        expense.setAmount(new Double(222.222));
        expense.setCurrency("USD");
        expense.setCreationDate(new Timestamp(System.currentTimeMillis()));
        return expense;
    }
    
    private Category createCategory(AppUser appUser) {

        Category category = new Category();
        category.setId(1);
        category.setName("Categ1");
        category.setUser(appUser);
        return category;
    }
    
    private CategoryDTO createCategoryDTO() {
        
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(11);
        categoryDTO.setName("CategoryDTO1");
        return categoryDTO;
    }
    
    private AppUser createAppUser(String email, String username) {
    
        AppUser appUser = new AppUser();
        appUser.setUserId(1);
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setUsername(username);
        appUser.setBirthdate(new Date());
        appUser.setEmail(email);
        return appUser;
    }
    
   
}
