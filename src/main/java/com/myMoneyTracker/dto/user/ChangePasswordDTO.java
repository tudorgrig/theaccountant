package com.myMoneyTracker.dto.user;


/**
 * Created by Florin on 08.08.2016.
 */
public class ChangePasswordDTO {

    private String op;
    private String np;

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getNp() {
        return np;
    }

    public void setNp(String np) {
        this.np = np;
    }
}
