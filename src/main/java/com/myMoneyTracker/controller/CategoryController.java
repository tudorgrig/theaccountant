package com.myMoneyTracker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myMoneyTracker.controller.exception.NotFoundException;
import com.myMoneyTracker.converter.CategoryConverter;
import com.myMoneyTracker.dao.AppUserDao;
import com.myMoneyTracker.dao.CategoryDao;
import com.myMoneyTracker.dto.category.CategoryDTO;
import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.user.AppUser;
import com.myMoneyTracker.util.ControllerUtil;

/**
 * Rest controller for Category entity
 *
 * @author Florin, on 25.12.2015
 */
@RestController
@RequestMapping(value = "/category")
public class CategoryController {
    
    private static final Logger log = Logger.getLogger(CategoryController.class.getName());
    
    @Autowired
    private CategoryDao categoryDao;
    
    @Autowired
    private AppUserDao appUserDao;
    
    @Autowired
    private CategoryConverter categoryConverter;
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody @Valid Category category) {
    
        String currentUsername = ControllerUtil.getCurrentLoggedUsername();
        AppUser user = appUserDao.findByUsername(currentUsername);
        if(user == null){
            throw new NotFoundException("User not found");
        }
        category.setUser(user);
        Category responseCategory = categoryDao.save(category);
        return new ResponseEntity<CategoryDTO>(categoryConverter.convertTo(responseCategory), HttpStatus.OK);
    }
    
    @RequestMapping(value = "find/{categoryName.+}", method = RequestMethod.GET)
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable("categoryName") String categoryName) {
    
        String username = ControllerUtil.getCurrentLoggedUsername();
        Category category = categoryDao.findByNameAndUsername(categoryName, username);
        if (category == null) {
            throw new NotFoundException("Category not found");
        }
        return new ResponseEntity<CategoryDTO>(categoryConverter.convertTo(category), HttpStatus.OK);
    }
    
    @RequestMapping(value = "find_all", method = RequestMethod.GET)
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
    
        String username = ControllerUtil.getCurrentLoggedUsername();
        List<Category> categoryList = categoryDao.findByUsername(username);
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
        String currentUsername = ControllerUtil.getCurrentLoggedUsername();
        AppUser appUser = appUserDao.findByUsername(currentUsername);
        if(appUser == null){
            throw new NotFoundException("User not found");
        }
        category.setUser(appUser);
        categoryDao.save(category);
        return new ResponseEntity<String>("Category updated", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteCategory(@PathVariable("id") Long id) {
    
        try {
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
    
        String username = ControllerUtil.getCurrentLoggedUsername();
        categoryDao.deleteAllByUsername(username);
        categoryDao.flush();
        return new ResponseEntity<String>("Categories deleted", HttpStatus.NO_CONTENT);
    }
    
    private List<CategoryDTO> getListOfCategoryDTOs(List<Category> categories) {
    
        List<CategoryDTO> categoryDTOs = new ArrayList<CategoryDTO>();
        for (Category category : categories) {
            categoryDTOs.add(categoryConverter.convertTo(category));
        }
        return categoryDTOs;
    }
}
