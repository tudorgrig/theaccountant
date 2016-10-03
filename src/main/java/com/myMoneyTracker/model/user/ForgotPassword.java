package com.myMoneyTracker.model.user;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Florin Iacob.
 * This class is the entity class for the forgot_password table
 */
@Entity
@Table(name = "forgot_password")
public class ForgotPassword {
    
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
