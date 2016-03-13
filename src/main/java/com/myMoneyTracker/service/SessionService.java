package com.myMoneyTracker.service;

import com.myMoneyTracker.app.authentication.AuthenticatedUser;

/**
 * An interface that provides support for a class in order to be used to by the application to register 
 * information about the users sessions.
 * 
 * @author Florin
 */
public interface SessionService {

    /**
     * Add a user that have passed the authentication security to the current list of authenticated users.
     * Remember, even if a user is registered to the application (he has a valid user account), he must be 
     * logged in to the application session to have the ability to make HTTP requests to the application.
     * 
     * @param authenticatedUser
     * @return
     *      true if the user have been inserted to the authenticated users list, or false otherwise.
     */
    boolean addAuthenticatedUser(AuthenticatedUser authenticatedUser);

    /**
     * Checks if the received authentication string is a valid authentication string that had been registered 
     * to the current application session. 
     * 
     * @param authenticationString
     * @return
     *      true if the user is authenticated, or false otherwise
     */
    boolean isAValidAuthenticationString(String authenticationString);
    
    /**
     * Extracts the username and the password of a user based on a basic valid authorization string;
     * 
     * @param authorizationString
     *      the string that had been used by the user to authenticate himself
     * @return
     *      a 2 indexed string array containing the username at index 0 and the password at 
     *      index 0, or an emtpy string array in case of a malformed authorization string.
     */
    String[] extractUsernameAndPassword(final String authorizationString);
    
    /**
     * Print all the users that are currently authenticated to the application
     */
    void printAutheticatedUsers();
    
}
