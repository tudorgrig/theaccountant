package com.myMoneyTracker.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.myMoneyTracker.model.category.Category;

/**
 * Created by florinIacob on 18.12.2015.
 * Data access object class for 'category' 
 */
@Transactional
public interface CategoryDao extends JpaRepository<Category, Long> {

	@Query("SELECT c FROM Category c WHERE c.name = ?1 AND c.user.username = ?2")
	Category findByNameAndUsername(String categoryName, String username);
	
	@Query("SELECT c FROM Category c WHERE c.user.username = ?1")
	List<Category> findByUsername(String username);
	
	//TODO: find a way to delete all categories bt username
//	@Query(value = "delete from category WHERE user_id IN (SELECT app_user.id FROM app_user WHERE app_user.username= ?0)", nativeQuery = true)
//	@Modifying
//	@Transactional
//	@Query("delete from Category c where c.user.username = ?1")
//	void deleteAllCategoriesForUser(String username);
	
}
