package com.myMoneyTracker.dao;

import com.myMoneyTracker.model.category.Category;
import com.myMoneyTracker.model.subcategory.Subcategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * @author Tudor Grigoriu
 * Test class for the subcategory dao
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/spring-config.xml"})
@Transactional
public class SubcategoryDaoTest {

    private static final Logger logger = Logger.getLogger(CategoryDaoTest.class.getName());

    @Autowired
    private SubcategoryDao subcategoryDao;

    @Autowired
    private CategoryDao categoryDao;

    @Test
    public void shouldSaveSubCategory() {
        Subcategory subcategory = createSubcategory();
        subcategory = subcategoryDao.save(subcategory);
        logger.info("The category has id = " + subcategory.getId());
        assertTrue(subcategory.getId() != 0);
    }

    @Test
    public void shouldFindCategory() {
        Subcategory subcategory = createSubcategory();
        subcategory = subcategoryDao.save(subcategory);
        subcategory = subcategoryDao.findOne(subcategory.getId());
        assertTrue(subcategory != null);
    }

    @Test
    public void shouldNotFindCategory() {
        Subcategory subcategory = subcategoryDao.findOne(new Random().nextLong());
        assertTrue(subcategory == null);
    }

    @Test
    public void shouldDeleteCategory() {
        Subcategory subcategory = createSubcategory();
        subcategory = subcategoryDao.save(subcategory);
        subcategoryDao.delete(subcategory);
        subcategory = subcategoryDao.findOne(subcategory.getId());
        assertTrue(subcategory == null);
    }

    @Test
    public void shouldUpdateSubcategory() {
        Subcategory subcategory = createSubcategory();
        subcategory = subcategoryDao.save(subcategory);
        Category category = createCategory();
        category.setName("Updated");
        subcategory.setCategory(category);
        subcategory = subcategoryDao.save(subcategory);
        assertTrue(subcategory.getCategory().getName().equals("Updated"));
    }

    @Test
    public void shouldSaveAndFlush() {
        Subcategory subcategory = createSubcategory();
        subcategory = subcategoryDao.save(subcategory);
        assertTrue(subcategory.getId() > 0);
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("Florin");
        return categoryDao.save(category);
    }


    private Subcategory createSubcategory() {
        Subcategory subcategory = new Subcategory();
        subcategory.setName("SUBCATEGORY");
        subcategory.setCategory(createCategory());
        return subcategory;
    }
}
