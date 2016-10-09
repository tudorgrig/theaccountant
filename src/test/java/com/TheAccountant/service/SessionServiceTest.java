package com.TheAccountant.service;

import java.sql.Timestamp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.TheAccountant.dao.AuthenticatedSessionDao;
import com.TheAccountant.model.session.AuthenticatedSession;

/**
 * Test class for {@link SessionService}
 *
 * @author Florin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
public class SessionServiceTest {
    
    private String username = "Florin1234";
    private String password = "Password1234";
    private String clientIpAddress = "1.1.1.1";
    private Timestamp expirationTime;
    private String authorizationString;
    private AuthenticatedSession authenticatedSession;
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private AuthenticatedSessionDao authenticatedSessionDao;
    
    @Before
    public void init() {
    
        authorizationString = sessionService.encodeUsernameAndPassword(username, password);
        expirationTime = sessionService.calculateExpirationTimeStartingFromNow();
        authenticatedSession = new AuthenticatedSession(authorizationString, username, clientIpAddress, expirationTime);
    }
    
    @After
    public void cleanUp() {
        
        authenticatedSessionDao.deleteAll();
        authenticatedSessionDao.flush();
    }
    
    @Test
    public void shouldAddAuthenticationSession() {
    
        boolean added = sessionService.addAuthenticatedSession(authenticatedSession);
        Assert.assertTrue("Cannot add AuthenticatedSession!", added);
    }
    
    @Test(expected = org.springframework.transaction.TransactionSystemException.class)
    public void shouldNotAddInvalidAuthenticationSession() {
    
        boolean added = sessionService.addAuthenticatedSession(new AuthenticatedSession());
        Assert.assertFalse(added);
    }
    
    @Test
    public void shouldRemoveAuthenticationSession() {
    
        boolean added = sessionService.addAuthenticatedSession(authenticatedSession);
        Assert.assertTrue("Cannot add AuthenticatedSession!", added);
        
        boolean removed = sessionService.removeAuthenticatedSession(authorizationString, clientIpAddress);
        Assert.assertTrue("Cannot remove AuthenticatedSession!", removed);
    }
    
    @Test
    public void shouldNotRemoveAuthenticationSession() {
    
        boolean removed = sessionService.removeAuthenticatedSession(authorizationString, clientIpAddress);
        Assert.assertFalse("Should not remove AuthenticatedSession!", removed);
    }
    
    @Test 
    public void shouldBeAValidAuthenticationSession() {
        
        boolean added = sessionService.addAuthenticatedSession(authenticatedSession);
        Assert.assertTrue("Cannot add AuthenticatedSession!", added);
        
        boolean isValid = sessionService.isAValidAuthenticationString(authorizationString, clientIpAddress);
        Assert.assertTrue(isValid);
    }
    
    @Test 
    public void shouldNotBeAValidAuthenticationSession() {
        
        boolean isValid = sessionService.isAValidAuthenticationString("Basic invalid_auth_string", clientIpAddress);
        Assert.assertFalse("Authentication String should not be valid!", isValid);
        
        isValid = sessionService.isAValidAuthenticationString("Basic invalid_auth_string", "100.100.100.100");
        Assert.assertFalse("Client IP Address should not be valid!", isValid);
        
        authenticatedSession.setExpirationTime(new Timestamp(System.currentTimeMillis() - 1000));
        sessionService.addAuthenticatedSession(authenticatedSession);
        isValid = sessionService.isAValidAuthenticationString(authorizationString, clientIpAddress);
        Assert.assertFalse("Should be an expired Authenticated Session!", isValid);
    }
    
    @Test
    public void shouldEncodeDecodeAuthorizationString() {
        
        String username = "my_username";
        String password = "my_password";
        String authorizationString = sessionService.encodeUsernameAndPassword(username, password);
        String credentials[] = sessionService.extractUsernameAndPassword(authorizationString);
        
        if (credentials.length == 2) {
            Assert.assertEquals(username, credentials[0]);
            Assert.assertEquals(password, credentials[1]);
        } else {
            Assert.fail("Credentails array should have 2 values!");
        }
    }
}
