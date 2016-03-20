package com.myMoneyTracker.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myMoneyTracker.model.session.AuthenticatedSession;

/**
 * Data access object class for authenticated_session table
 * 
 * Created by Florin Iacob.
 */
@Transactional
@Repository
public interface AuthenticatedSessionDao extends JpaRepository<AuthenticatedSession, Long> {
    
    List<AuthenticatedSession> findByAuthorizationString(String authorization);
    
    List<AuthenticatedSession> findByAuthorizationStringAndIpAddress(String authorization, String ipAddress);
    
}
