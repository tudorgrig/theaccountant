package com.myMoneyTracker.dto.user;


/**
 * Created by tudor.grigoriu on 10.01.2016.
 */
public class AppUserDTO {

    private long id;
    private String firstName;
    private String surname;
    private String username;

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    public String getSurname() {

        return surname;
    }

    public void setSurname(String surname) {

        this.surname = surname;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }
}
