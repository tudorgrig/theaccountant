package com.myMoneyTracker.dao;

import com.myMoneyTracker.model.session.AuthenticatedSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Data access object class for authenticated_session table
 * 
 * Created by Florin Iacob.
 */
@Transactional
@Repository
public interface AuthenticatedSessionDao extends JpaRepository<AuthenticatedSession, Long> {
    
    List<AuthenticatedSession> findByAuthorizationStringAndIpAddress(String authorization, String ipAddress);
    
}
