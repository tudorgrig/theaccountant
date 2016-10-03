package com.myMoneyTracker.dto.user;


/**
 * Created by Floryn on 08.08.2016.
 */
public class ForgotPasswordDTO {

    private String email;

    public ForgotPasswordDTO() {}

    public ForgotPasswordDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
