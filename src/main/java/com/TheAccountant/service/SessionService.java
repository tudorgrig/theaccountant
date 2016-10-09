package com.TheAccountant.service;

import com.TheAccountant.model.session.AuthenticatedSession;

import java.sql.Timestamp;

/**
 * An interface that provides support for a class in order to be used to by the application to register 
 * information about the users sessions.
 * 
 * @author Florin
 */
public interface SessionService {

    /**
     * Add a session of a user that have passed the authentication security to the current list of 
     * active sessions.
     * Remember, even if a user is registered to the application (he has a valid user account), he must be 
     * logged in to the application session to have the ability to make HTTP requests to the application.
     * 
     * @param authenticatedSession
     * @return
     *      true if the session have been inserted to the active sessions list, or false otherwise.
     */
    boolean addAuthenticatedSession(AuthenticatedSession authenticatedSession);
    
    /**
     * Removes an authenticated session of a user from active sessions list, based on his authorization string 
     * and the ipAddress used to login into the application.
     * 
     * @param authorizationString
     *      the string used to authenticate the user
     * @param clientIpAddress
     *      request IP address
     * @return
     *      true if the session was found and has been removed, or false otherwise.
     */
    boolean removeAuthenticatedSession(String authorizationString, String clientIpAddress);

    /**
     * Checks if the received authorization string is a valid authentication string that had been registered 
     * to the current application session. 
     * 
     * @param authorizationString
     *      user's authorization string
     * @param clientIpAddress
     *      IP address used to login the user
     * @return
     *      true if the user is authenticated, or false otherwise
     */
    boolean isAValidAuthenticationString(String authorizationString, String clientIpAddress);
    
    /**
     * Extracts the username and the password of a user based on a basic valid authorization string;
     * 
     * @param authorizationString
     *      the string that had been used by the user to authenticate himself
     * @return
     *      a 2 indexed string array containing the username at index 0 and the password at 
     *      index 0, or an empty string array in case of a malformed authorization string.
     */
    String[] extractUsernameAndPassword(final String authorizationString);
    
    /**
     * Method that can be used in order to calculate an expiration time starting from now.
     * The expiration time can be set for an {@link AuthenticatedSession} created now.
     * 
     * @return
     *      a {@link Timestamp} instance representing the expiration time
     */
    Timestamp calculateExpirationTimeStartingFromNow();

    /**
     * Creates an authorization string based on the received credentials.
     * For a basic authorization string, the result will also contain the 'Basic' prefix. 
     * 
     * @param username
     * @param password
     * @return
     */
    String encodeUsernameAndPassword(String username, String password);
    
    /**
     * Method that will be scheduled in order to clean up expired sessions from the database.
     */
    void scheduleAuthenticatedSessionsCleanUp();
}
