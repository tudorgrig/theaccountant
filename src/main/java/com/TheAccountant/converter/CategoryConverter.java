package com.TheAccountant.converter;

import com.TheAccountant.dto.category.CategoryDTO;
import com.TheAccountant.model.category.Category;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

/**
 * Dozer converter between category and categoryDTO
 * 
 * @author Florin
 */
public class CategoryConverter {
    
    public CategoryDTO convertTo(Category category) {
    
        CategoryDTO categoryDTO = new CategoryDTO();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(category, categoryDTO);
        return categoryDTO;
        
    }
    
    public Category convertFrom(CategoryDTO categoryDTO) {
    
        Category category = new Category();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(categoryDTO, category);
        return category;
    }
}
