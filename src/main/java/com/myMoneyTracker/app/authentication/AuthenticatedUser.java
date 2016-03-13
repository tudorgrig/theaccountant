package com.myMoneyTracker.app.authentication;

import java.sql.Timestamp;

/**
 * A class that can be used by the application to register information about the session of a
 * user that have been logged in to the application 
 * 
 * @author Florin
 */
public class AuthenticatedUser {
    
    private static final long FIVE_DAYS = 5 * 24 * 60 * 60 * 1000;
    
    private String username;
    private String authenticationString;
    private String ipAddress;
    private Timestamp expirationTime;
    
    /**
     * 
     * @param username
     *      the name of the authenticated user
     * @param authenticationString
     *      the string that can be used to authenticate a user
     * @param ipAddress
     *      the IP address that had been used by the user to authenticate himself into the current session
     */
    public AuthenticatedUser(String username, String authenticationString, String ipAddress) {
        this.username = username;
        this.authenticationString = authenticationString;
        this.ipAddress = ipAddress;
        this.expirationTime = new Timestamp(System.currentTimeMillis() + FIVE_DAYS);
    }
    
    public String getAuthenticationString() {
    
        if (!isExpired()) {
            return authenticationString;
        } else {
            return null;
        }
    }
    
    public String getUsername() {
    
        if (!isExpired()) {
            return username;
        } else {
            return null;
        }
    }

    public String getIpAddress() {
    
        return ipAddress;
    }
    
    public boolean isExpired() {
        return expirationTime.getTime() < System.currentTimeMillis();
    }
}
