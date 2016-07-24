package com.myMoneyTracker.controller;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import com.myMoneyTracker.dao.ExpenseDao;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myMoneyTracker.controller.exception.ConflictException;
import com.myMoneyTracker.controller.exception.NotFoundException;
import com.myMoneyTracker.converter.CategoryConverter;
import com.myMoneyTracker.dao.CategoryDao;
import com.myMoneyTracker.dto.category.CategoryDTO;
import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.UserUtil;

/**
 * Rest controller for Category entity
 *
 * @author Florin, on 25.12.2015
 */
@RestController
@RequestMapping(value = "/category")
public class CategoryController {
    
    @Autowired
    private CategoryDao categoryDao;
    
    @Autowired
    private CategoryConverter categoryConverter;
    
    @Autowired
    private UserUtil userUtil;

    @Autowired
    private ExpenseDao expenseDao;
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody @Valid Category category) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        category.setUser(user);
        try {
            Category responseCategory = categoryDao.saveAndFlush(category);
            return new ResponseEntity<CategoryDTO>(categoryConverter.convertTo(responseCategory), HttpStatus.OK);
        } catch (DataIntegrityViolationException dive) {
            throw new ConflictException(dive.getMostSpecificCause().getMessage());
        }

    }
    
    @RequestMapping(value = "find/{categoryName.+}", method = RequestMethod.GET)
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable("categoryName") String categoryName) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        Category category = categoryDao.findByNameAndUsername(categoryName, user.getUsername());
        if (category == null) {
            throw new NotFoundException("Category not found");
        }
        return new ResponseEntity<CategoryDTO>(categoryConverter.convertTo(category), HttpStatus.OK);
    }
    
    @RequestMapping(value = "find_all", method = RequestMethod.GET)
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        List<Category> categoryList = categoryDao.findByUsername(user.getUsername());
        if (categoryList.isEmpty()) {
            return new ResponseEntity<List<CategoryDTO>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<CategoryDTO>>(getListOfCategoryDTOs(categoryList), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> updateCategory(@PathVariable("id") Long id, @RequestBody Category category) {
    
        Category oldCategory = categoryDao.findOne(id);
        if (oldCategory == null) {
            throw new NotFoundException("Category not found");
        }
        category.setId(id);
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        category.setUser(user);
        categoryDao.save(category);
        return new ResponseEntity<String>("Category updated", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteCategory(@PathVariable("id") Long id) {
    
        try {
            AppUser user = userUtil.extractLoggedAppUserFromDatabase();
            Category category = categoryDao.findOne(id);
            if(category == null){
                throw new NotFoundException("Category not found");
            }
            expenseDao.deleteAllByCategoryNameAndUsername(category.getName(),user.getUsername());
            categoryDao.delete(id);
            categoryDao.flush();
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new NotFoundException(emptyResultDataAccessException.getMessage());
        }
        return new ResponseEntity<String>("Category deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete_all", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteAll() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        categoryDao.deleteAllByUsername(user.getUsername());
        categoryDao.flush();
        return new ResponseEntity<String>("Categories deleted", HttpStatus.NO_CONTENT);
    }
    
    private List<CategoryDTO> getListOfCategoryDTOs(List<Category> categories) {
    
        List<CategoryDTO> categoryDTOs = new ArrayList<CategoryDTO>();
        categories.stream().forEach(category -> {
            categoryDTOs.add(categoryConverter.convertTo(category));
        });
        return categoryDTOs;
    }
}
