package com.myMoneyTracker.app.service;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.myMoneyTracker.model.user.AppUser;

/**
 * A class representing an implementation of the {@link Authentication} interface.
 * This class can be used to register informations about the current user's session.
 * 
 * @author Florin
 */
public class SessionAuthentication implements Authentication {
    
    private static final long serialVersionUID = -7906839299823126986L;
    
    private static final long SEVEN_DAYS = 1000 * 60 * 60 * 24 * 7;
    
    private String name;
    private boolean isAuthenticated;
    private AppUser user;
    private String sessionToken;
    private long tokenExpirationTimeMs;
    
    public SessionAuthentication(AppUser appUser, String sessionToken) {
    
        if (appUser != null) {
            this.user = appUser;
            this.name = appUser.getUsername();
            this.isAuthenticated = true;
            this.setSessionToken(sessionToken);
        }
    }

    /**
     * Get the user name for the current session.
     * Returns null if the current session doesn't have a registered user.
     */
    public String getName() {
    
        return this.name;
    }
    
    /**
     * Set the current user name for the current session.
     */
    public void setName(String name) {
    
        this.name = name;
    }
    
    public Collection<? extends GrantedAuthority> getAuthorities() {
    
        return null;
    }
    
    public Object getCredentials() {
    
        return null;
    }
    
    public Object getDetails() {
    
        return null;
    }
    
    /**
     * Get the current user logged.
     * 
     * @return {@link AppUser} representing the user logged.
     */
    public AppUser getPrincipal() {
    
        return this.user;
    }
    
    /**
     * Set the current user logged.
     */
    public void setPrincipal(AppUser user) {
    
        this.user = user;
    }
    
    /**
     * @return : true if a user is registered to the current session or false otherwise.
     */
    public boolean isAuthenticated() {
    
        return this.isAuthenticated;
    }
    
    /**
     * Set if an user is authenticated to the current session.
     */
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    
        this.isAuthenticated = isAuthenticated;
    }

    /**
     * 
     * @return : the session token for the current user logged.
     */
    public String getSessionToken() {
    
        if (System.currentTimeMillis() > tokenExpirationTimeMs) {
            this.sessionToken = null;
        }
        return this.sessionToken;
    }

    /**
     * 
     * @param sessionToken : the session token for the current user logged.
     */
    public void setSessionToken(String sessionToken) {
    
        tokenExpirationTimeMs = System.currentTimeMillis() + SEVEN_DAYS;
        this.sessionToken = sessionToken;
    }
    
}
