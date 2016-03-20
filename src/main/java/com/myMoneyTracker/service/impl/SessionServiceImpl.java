package com.myMoneyTracker.service.impl;

import java.nio.charset.Charset;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;

import com.myMoneyTracker.dao.AuthenticatedSessionDao;
import com.myMoneyTracker.model.session.AuthenticatedSession;
import com.myMoneyTracker.service.SessionService;

@Service
public class SessionServiceImpl implements SessionService {
    
    private static final long FIVE_DAYS_IN_MILLISECONDS = 5 * 24 * 60 * 60 * 1000;
    
    @Autowired
    private AuthenticatedSessionDao authenticatedSessionDao;
    
    public boolean addAuthenticatedSession(AuthenticatedSession authenticatedSession) {
        
        authenticatedSession = authenticatedSessionDao.save(authenticatedSession);
        boolean inserted = authenticatedSession.getId() > 0;
        return inserted;
    }

    public boolean removeAuthenticatedSession(String authorizationString, String clientIpAddress) {
    
        AuthenticatedSession authenticatedSession = authenticatedSessionDao.
                findByAuthorizationStringAndIpAddress(authorizationString, clientIpAddress).get(0);
        boolean found = authenticatedSession != null;
        if (found) {
            authenticatedSessionDao.delete(authenticatedSession.getId());
        }
        return found;
    }

    public boolean isAValidAuthenticationString(String authorizationString, String clientIpAddress) {
    
        AuthenticatedSession authenticatedSession = authenticatedSessionDao.
                findByAuthorizationStringAndIpAddress(authorizationString, clientIpAddress).get(0);
        boolean found = authenticatedSession != null;
        if (found) {
            boolean isExpired = authenticatedSession.getExpirationTime().before(
                    new Timestamp(System.currentTimeMillis()));
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
            String credentials = new String(Base64.decode(base64Credentials.getBytes()),
                    Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":",2);
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
}
