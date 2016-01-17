package com.myMoneyTracker.model.user;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Florin Iacob.
 * This class is the entity class for the user_registration
 */
@Entity
@Table(name = "user_registration")
public class UserRegistration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String code;
    
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    @NotNull
    private AppUser user;

    public long getId() {
    
        return id;
    }

    public void setId(long id) {
    
        this.id = id;
    }

    public String getCode() {
    
        return code;
    }

    public void setCode(String code) {
    
        this.code = code;
    }

    public AppUser getUser() {
    
        return user;
    }

    public void setUser(AppUser user) {
    
        this.user = user;
    }
}
