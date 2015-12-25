package com.myMoneyTracker.dao;

import com.myMoneyTracker.model.subcategory.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Tudor Grigoriu
 * Data access object for subcategory
 */
public interface SubcategoryDao extends JpaRepository<Subcategory, Long> {
}
