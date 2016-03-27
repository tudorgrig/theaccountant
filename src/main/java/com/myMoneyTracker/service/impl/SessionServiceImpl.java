package com.myMoneyTracker.service.impl;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import com.myMoneyTracker.dao.AuthenticatedSessionDao;
import com.myMoneyTracker.model.session.AuthenticatedSession;
import com.myMoneyTracker.service.SessionService;

@EnableAsync
@EnableScheduling
@Service
@Transactional
public class SessionServiceImpl implements SessionService {
    
    private static final long FIVE_DAYS_IN_MILLISECONDS = 5 * 24 * 60 * 60 * 1000;
    private static final long TWELVE_HOURS_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    
    @Autowired
    private AuthenticatedSessionDao authenticatedSessionDao;
    
    public boolean addAuthenticatedSession(AuthenticatedSession authenticatedSession) throws TransactionSystemException {
    
        try {
            authenticatedSession = authenticatedSessionDao.saveAndFlush(authenticatedSession);
        } catch (Exception e) {
            throw new TransactionSystemException(e.getMessage());
        }
        boolean inserted = authenticatedSession.getId() > 0;
        return inserted;
    }
    
    public boolean removeAuthenticatedSession(String authorizationString, String clientIpAddress) {
    
        AuthenticatedSession authenticatedSession = null;
        List<AuthenticatedSession> sessions = authenticatedSessionDao.findByAuthorizationStringAndIpAddress(authorizationString, clientIpAddress);
        if (sessions != null && !sessions.isEmpty()) {
            authenticatedSession = sessions.get(0);
        }
        boolean found = authenticatedSession != null;
        if (found) {
            authenticatedSessionDao.delete(authenticatedSession.getId());
        }
        return found;
    }
    
    public boolean isAValidAuthenticationString(String authorizationString, String clientIpAddress) {
    
        AuthenticatedSession authenticatedSession = null;
        List<AuthenticatedSession> sessions = authenticatedSessionDao.findByAuthorizationStringAndIpAddress(authorizationString, clientIpAddress);
        if (sessions != null && !sessions.isEmpty()) {
            authenticatedSession = sessions.get(0);
        }
        boolean found = authenticatedSession != null;
        if (found) {
            boolean isExpired = isSessionExpired(authenticatedSession);
            if (isExpired) {
                removeAuthenticatedSession(authorizationString, clientIpAddress);
                found = false;
            }
        }
        return found;
    }
    
    public String[] extractUsernameAndPassword(final String authorizationString) {
    
        if (authorizationString != null && authorizationString.startsWith("Basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorizationString.substring("Basic".length()).trim();
            String credentials = new String(Base64.decode(base64Credentials.getBytes()), Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            return values;
        } else {
            return new String[] {};
        }
    }
    
    public String encodeUsernameAndPassword(String username, String password) {
    
        String credentials = username + ":" + password;
        String authorizationString = new String(Base64.encode(credentials.getBytes()));
        return "Basic " + authorizationString;
    }
    
    public Timestamp calculateExpirationTimeStartingFromNow() {
    
        return new Timestamp(System.currentTimeMillis() + FIVE_DAYS_IN_MILLISECONDS);
    }
    
    @Scheduled(fixedDelay = TWELVE_HOURS_IN_MILLISECONDS)
    public void scheduleAuthenticatedSessionsCleanUp() {
    
        List<AuthenticatedSession> authenticatedSessions = authenticatedSessionDao.findAll();
        for (AuthenticatedSession authenticatedSession : authenticatedSessions) {
            
            if (isSessionExpired(authenticatedSession)) {
                authenticatedSessionDao.delete(authenticatedSession.getId());
            }
        }
    }
    
    private boolean isSessionExpired(AuthenticatedSession authenticatedSession) {
    
        return authenticatedSession.getExpirationTime().before(new Timestamp(System.currentTimeMillis()));
    }
}
