package com.TheAccountant.model.user;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Florin Iacob.
 * This class is the entity class for the forgot_password table
 */
@Entity
@Table(name = "forgot_password")
public class ForgotPassword {
    

    private long id;
    private String code;
    private AppUser user;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
    
        return id;
    }

    public void setId(long id) {
    
        this.id = id;
    }

    @NotNull
    public String getCode() {
    
        return code;
    }

    public void setCode(String code) {
    
        this.code = code;
    }

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    @NotNull
    public AppUser getUser() {
    
        return user;
    }

    public void setUser(AppUser user) {
    
        this.user = user;
    }
}
