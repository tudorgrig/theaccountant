package com.TheAccountant.dto.counterparty;

import com.TheAccountant.model.user.AppUser;

/**
 * Created by tudor.grigoriu on 3/18/2017.
 */
public class CounterpartyDTO {

    private long id;
    private AppUser user;
    private String name;
    private String email;
    private double total;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
