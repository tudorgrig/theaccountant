package com.myMoneyTracker.service.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;

import com.myMoneyTracker.app.authentication.AuthenticatedUser;
import com.myMoneyTracker.service.SessionService;

@Service
public class SessionServiceImpl implements SessionService {
    
    private List<AuthenticatedUser> authenticatedUsers = new ArrayList<AuthenticatedUser>();
    
    public boolean addAuthenticatedUser(AuthenticatedUser authenticatedUser) {
        return authenticatedUsers.add(authenticatedUser);
    }
    
    // TODO require also the ipAdrress
    public boolean isAValidAuthenticationString(String authenticationString) {
        boolean result = false;
        
        for (AuthenticatedUser user : authenticatedUsers) {
            if (user.getAuthenticationString().equals(authenticationString)) {
                if (user.isExpired()) {
                    authenticatedUsers.remove(user);
                } else {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
    
    public void printAutheticatedUsers() {
        for (AuthenticatedUser user : authenticatedUsers) {
            System.out.println("        - auth user: " + user.getUsername());
        }
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
    
    /**
     * Method executed once every 12 hours in order to clean up the authenticated users list
     */
    @Scheduled(fixedDelay=(12 * 60 * 60 * 1000))
    public void cleanExpiredSessions() {
        for (AuthenticatedUser user : authenticatedUsers) {
           if (user.isExpired()) {
               authenticatedUsers.remove(user);
           }
        }
    }
}
