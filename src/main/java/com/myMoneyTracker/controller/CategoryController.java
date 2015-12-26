package com.myMoneyTracker.controller;

import java.util.List;
import java.util.logging.Logger;

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

import com.myMoneyTracker.dao.AppUserDao;
import com.myMoneyTracker.dao.CategoryDao;
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
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Category> createCategory(@RequestBody @Valid Category category) {
    	String currentUsername = ControllerUtil.getCurrentLoggedUsername();
    	System.out.println(" >>>> Username: " + currentUsername);
    	AppUser user = appUserDao.findUserByUsername(currentUsername);
    	category.setUser(user);
    	Category responseCategory = categoryDao.save(category);
    	return new ResponseEntity<Category>(responseCategory, HttpStatus.OK);
    }
    
    @RequestMapping(value = "find/{cetegoryName}", method = RequestMethod.GET)
    public ResponseEntity<?> getCategory(@PathVariable("name") String categoryName) {
    	String username = ControllerUtil.getCurrentLoggedUsername();
    	Category category = categoryDao.findCategoryByNameAndUsername(categoryName, username);
    	if(category == null){
            return new ResponseEntity<String>("Category not found", HttpStatus.NOT_FOUND);
        }
    	return new ResponseEntity<Category>(category, HttpStatus.OK);
    }
    
    @RequestMapping(value = "find_all", method = RequestMethod.GET)
    public ResponseEntity<?> getAllCategories() {
    	String username = ControllerUtil.getCurrentLoggedUsername();
    	List<Category> categoryList = categoryDao.findCategoriesByUsername(username);
    	if(categoryList.isEmpty()){
            return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
        }
    	return new ResponseEntity<List<Category>>(categoryList, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> updateCategory(@PathVariable("id") Long id, @RequestBody Category category){
        Category oldCategory = categoryDao.findOne(id);
        if(oldCategory == null){
            return new ResponseEntity<String>("Category not found", HttpStatus.NOT_FOUND);
        }
        category.setId(id);
        category.setUser(oldCategory.getUser());
        categoryDao.save(category);
        return new ResponseEntity<String>("Category updated", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteCategory(@PathVariable("id") Long id){
        try {
            categoryDao.delete(id);
        }catch(EmptyResultDataAccessException emptyResultDataAccessException){
            log.info(emptyResultDataAccessException.getMessage());
            return new ResponseEntity<String>(emptyResultDataAccessException.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>("Category deleted", HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/deleteAll/", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAll(){
    	String username = ControllerUtil.getCurrentLoggedUsername();
    	List<Category> categoryList = categoryDao.findCategoriesByUsername(username);
    	for (Category c : categoryList) {
    		categoryDao.delete(c);
    	}
    	//categoryDao.deleteAllCategoriesForUser(username);
        return new ResponseEntity<String>("Categories deleted", HttpStatus.NO_CONTENT);
    }
}
