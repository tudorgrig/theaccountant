package com.myMoneyTracker.model.session;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * A class that can be used by the application to register information about the session of a
 * user that have been logged in to the application 
 * 
 * @author Florin Iacob.
 */
// TODO to be added unique constraint
//uniqueConstraints = {@UniqueConstraint(columnNames = { "authorization_string" }), 
//        @UniqueConstraint(columnNames = { "ipAddress" })},
@Entity
@Table(name = "authenticated_session",
    indexes = {@Index(name = "authorization_string_index",  columnList="authorization_string", unique = false)
})
public class AuthenticatedSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @NotEmpty
    @Column(name = "authorization_string")
    private String authorizationString;
    
    @NotNull
    @NotEmpty
    private String username;
    
    @NotNull
    @NotEmpty
    private String ipAddress;
    
    @NotNull
    private Timestamp expirationTime;
    
    public AuthenticatedSession() {}
    
    public AuthenticatedSession(String authorization, String username, String ipAddress, Timestamp expirationTime) {
    
        this.authorizationString = authorization;
        this.username = username;
        this.ipAddress = ipAddress;
        this.expirationTime = expirationTime;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }
    
    public String getAuthorization() {
    
        return authorizationString;
    }
    
    public void setAuthorization(String authorization) {
    
        this.authorizationString = authorization;
    }
    
    public String getUsername() {
    
        return username;
    }
    
    public void setUsername(String username) {
    
        this.username = username;
    }
    
    public String getIpAddress() {
    
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
    
        this.ipAddress = ipAddress;
    }
    
    public Timestamp getExpirationTime() {
    
        return expirationTime;
    }
    
    public void setExpirationTime(Timestamp expirationTime) {
    
        this.expirationTime = expirationTime;
    }
}
