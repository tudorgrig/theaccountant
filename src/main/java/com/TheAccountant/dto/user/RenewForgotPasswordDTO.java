package com.TheAccountant.dto.user;


/**
 * Created by Florin on 08.08.2016.
 */
public class RenewForgotPasswordDTO {

    private String code;
    private String np;  // new password

    public RenewForgotPasswordDTO() {}

    public RenewForgotPasswordDTO(String code, String np) {
        this.code = code;
        this.np = np;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNp() {
        return np;
    }

    public void setNp(String np) {
        this.np = np;
    }
}
