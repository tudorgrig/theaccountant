package com.myMoneyTracker.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.myMoneyTracker.model.session.AuthenticatedSession;
import com.myMoneyTracker.service.SessionService;

/**
 * 
 * Test class for {@link AuthenticatedSessionDao} class.
 * 
 * @author Florin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
public class AuthenticatedSessionDaoTest {
    
    private final static String USERNAME = "my_username";
    private final static String PASSWORD = "my_password";
    
    @Autowired
    private AuthenticatedSessionDao authenticatedSessionDao;
    
    @Autowired
    private SessionService sessionService;

    @Test
    public void shouldSaveAuthenticatedSession() {
    
        AuthenticatedSession authenticatedSession = createAuthenticatedSession(USERNAME, PASSWORD);
        authenticatedSession = authenticatedSessionDao.save(authenticatedSession);
        Assert.assertTrue(authenticatedSession.getId() > 0);
    }
    
    @Test(expected = org.springframework.transaction.TransactionSystemException.class)
    public void shouldNotSaveAuthenticationSessionWithoutUsername() {
    
        AuthenticatedSession authenticatedSession = createAuthenticatedSession(USERNAME, PASSWORD);
        authenticatedSession.setUsername(null);
        authenticatedSessionDao.save(authenticatedSession);
    }
    
    @Test(expected = org.springframework.transaction.TransactionSystemException.class)
    public void shouldNotSaveAuthenticationSessionWithoutAuthorizationString() {
    
        AuthenticatedSession authenticatedSession = createAuthenticatedSession(USERNAME, PASSWORD);
        authenticatedSession.setAuthorization(null);
        authenticatedSessionDao.save(authenticatedSession);
    }
    
    @Test(expected = org.springframework.transaction.TransactionSystemException.class)
    public void shouldNotSaveAuthenticationSessionWithoutClientIp() {
    
        AuthenticatedSession authenticatedSession = createAuthenticatedSession(USERNAME, PASSWORD);
        authenticatedSession.setIpAddress(null);
        authenticatedSessionDao.save(authenticatedSession);
    }
    
    @Test(expected = org.springframework.transaction.TransactionSystemException.class)
    public void shouldNotSaveAuthenticationSessionWithoutExpirationTime() {
    
        AuthenticatedSession authenticatedSession = createAuthenticatedSession(USERNAME, PASSWORD);
        authenticatedSession.setExpirationTime(null);
        authenticatedSessionDao.save(authenticatedSession);
    }
    
    @Test
    public void shouldFindByAuthorizationStringAndClientIp() {
        
        AuthenticatedSession authenticatedSession = createAuthenticatedSession(USERNAME, PASSWORD);
        authenticatedSession = authenticatedSessionDao.save(authenticatedSession);
        Assert.assertTrue(authenticatedSession.getId() > 0);
        
        List<AuthenticatedSession> extractedAuthenticatedSessions = 
                authenticatedSessionDao.findByAuthorizationStringAndIpAddress(
                authenticatedSession.getAuthorization(), authenticatedSession.getIpAddress());
        Assert.assertTrue(extractedAuthenticatedSessions != null && !extractedAuthenticatedSessions.isEmpty());
    }
    
    private AuthenticatedSession createAuthenticatedSession(String username, String password) {
    
        String authorizationString = sessionService.encodeUsernameAndPassword(username, password);
        AuthenticatedSession authenticatedSession = new AuthenticatedSession();
        authenticatedSession.setAuthorization(authorizationString);
        authenticatedSession.setUsername(username);
        authenticatedSession.setIpAddress("100.100.100.100");
        authenticatedSession.setExpirationTime(sessionService.calculateExpirationTimeStartingFromNow());
        return authenticatedSession;
    }
}
