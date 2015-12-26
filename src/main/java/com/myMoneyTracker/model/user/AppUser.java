package com.myMoneyTracker.model.user;

import javax.persistence.*;
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
    private String firstName;
    private String surname;
    private Date birthdate;
    
    @Column(unique=true)
    private String username;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}