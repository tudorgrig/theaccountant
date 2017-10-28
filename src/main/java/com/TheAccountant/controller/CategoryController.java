package com.TheAccountant.controller;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.controller.exception.ConflictException;
import com.TheAccountant.controller.exception.NotFoundException;
import com.TheAccountant.converter.CategoryConverter;
import com.TheAccountant.dao.CategoryDao;
import com.TheAccountant.dto.category.CategoryDTO;
import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.payment.PaymentType;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.service.PaymentService;
import com.TheAccountant.service.exception.ServiceException;
import com.TheAccountant.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

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
    private PaymentService paymentService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody @Valid Category category) {

        try {
            if (category.getThreshold() > 0.0) {
                ChargeDTO chargeResult = paymentService.getPaymentStatus(PaymentType.USER_LICENSE);
                if (chargeResult.getPaymentApproved() == false) {
                    throw new BadRequestException("Category limit is allowed only for paid accounts!");
                }
            }

            AppUser user = userUtil.extractLoggedAppUserFromDatabase();
            category.setUser(user);
            Category responseCategory = categoryDao.saveAndFlush(category);
            return new ResponseEntity<>(categoryConverter.convertTo(responseCategory), HttpStatus.OK);
        } catch (DataIntegrityViolationException dive) {
            throw new ConflictException(dive.getMostSpecificCause().getMessage());
        } catch (ServiceException e) {
            throw new BadRequestException(e.getMessage());
        }

    }
    
    @RequestMapping(value = "find/{id.+}", method = RequestMethod.GET)
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable("id") long id) {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        Category category = categoryDao.findOne(id);
        if (category == null) {
            throw new NotFoundException("Category not found");
        } else if (category.getUser().getUserId() != user.getUserId()) {
            throw new BadRequestException("Invalid category request!");
        }

        return new ResponseEntity<>(categoryConverter.convertTo(category), HttpStatus.OK);
    }
    
    @RequestMapping(value = "find_all", method = RequestMethod.GET)
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        List<Category> categories = categoryDao.findByUsername(user.getUsername());

        if (categories == null || categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(getListOfCategoryDTOs(new HashSet<>(categories)), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> updateCategory(@PathVariable("id") Long id, @RequestBody Category category) {

        try {
            AppUser user = userUtil.extractLoggedAppUserFromDatabase();
            Category oldCategory = categoryDao.findOne(id);
            if (oldCategory == null) {
                throw new NotFoundException("Category not found");
            }

            if (category.getThreshold() != null && !category.getThreshold().equals(oldCategory.getThreshold())) {
                ChargeDTO chargeResult = paymentService.getPaymentStatus(PaymentType.USER_LICENSE);
                if (chargeResult.getPaymentApproved() == false) {
                    throw new BadRequestException("Category limit is allowed only for paid accounts!");
                }
            }

            category.setId(id);
            category.setUser(user);
            categoryDao.save(category);
        } catch (ServiceException e) {
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>("Category updated", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteCategory(@PathVariable("id") Long id) {
    
        try {
            userUtil.extractLoggedAppUserFromDatabase();
            Category category = categoryDao.findOne(id);
            if(category == null){
                throw new NotFoundException("Category not found");
            }
            categoryDao.delete(id);
            categoryDao.flush();
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new NotFoundException(emptyResultDataAccessException.getMessage());
        }
        return new ResponseEntity<>("Category deleted", HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/delete_all", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteAll() {
    
        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        categoryDao.deleteAllByUsername(user.getUsername());
        categoryDao.flush();
        return new ResponseEntity<>("Categories deleted", HttpStatus.NO_CONTENT);
    }
    
    private List<CategoryDTO> getListOfCategoryDTOs(Set<Category> categories) {
    
        List<CategoryDTO> categoryDTOs = new ArrayList<CategoryDTO>();
        categories.stream().forEach(category -> {
            categoryDTOs.add(categoryConverter.convertTo(category));
        });
        return categoryDTOs;
    }
}
