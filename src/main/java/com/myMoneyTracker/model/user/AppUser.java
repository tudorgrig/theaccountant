package com.myMoneyTracker.model.user;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by tudor.grigoriu on 17.12.2015.
 * Entity class for the app_user table
 */
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @NotNull
    private String firstName;
    @NotNull
    private String surname;
    private Date birthdate;
    @NotNull
    @Length(min=8, message="Password should have at least 8 characters")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
}