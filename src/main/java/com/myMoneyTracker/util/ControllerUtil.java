package com.myMoneyTracker.util;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.myMoneyTracker.app.authentication.SessionAuthentication;

/**
 * Class that contains useful methods for REST controllers.
 *
 * @author Florin, on 25.12.2015
 */
public class ControllerUtil {
    
    /**
     * Get the name of the user that is currently logged into the application.
     *
     * @return
     */
    public static String getCurrentLoggedUsername() {
    
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return username;
    }
    
    /**
     * Get the client ip address that was used for logging into the application.
     * 
     * @return
     */
    public static String getRequestClienIpAddress() {
        
        SessionAuthentication sessionAuthentication = 
                (SessionAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String clientIpAddress = null;
        if (sessionAuthentication != null) {
            clientIpAddress = sessionAuthentication.getClientIpAddress();
        }
        return clientIpAddress;
    }
    
    /**
     * Method that can be used to register a user for current session, useful in unit tests.
     *
     * @param username
     */
    public static void setCurrentLoggedUser(final String username) {
    
        Authentication authentication = new Authentication() {
            
            public String getName() {
            
                return username;
            }
            
            public void setAuthenticated(boolean arg0) throws IllegalArgumentException {
            
            }
            
            public boolean isAuthenticated() {
            
                return true;
            }
            
            public Object getPrincipal() {
            
                return null;
            }
            
            public Object getDetails() {
            
                return null;
            }
            
            public Object getCredentials() {
            
                return null;
            }
            
            public Collection<? extends GrantedAuthority> getAuthorities() {
            
                return null;
            }
        };
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
