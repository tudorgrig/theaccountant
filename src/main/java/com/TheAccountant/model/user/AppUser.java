package com.TheAccountant.model.user;

import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.income.Income;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tudor on 17.12.2015.
 * Entity class for the app_user table
 */
@Entity
@Table(name = "app_user",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }),
                @UniqueConstraint(columnNames = { "email" }) },
        indexes = {@Index(name = "username_index",  columnList="username", unique = true),
                @Index(name="email_index", columnList = "email", unique = true)})
public class AppUser {

    @Id
    @Column(name = "userId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;


    @Column(name = "firstName", unique = false, nullable = true)
    private String firstName;

    @Column(name = "surname", unique = false, nullable = true)
    private String surname;


    @Temporal(value = TemporalType.DATE)
    @Column(name = "birthdate", unique = false, nullable = true)
    private Date birthdate;


    @Column(name = "username", unique = true, nullable = false)
    @NotNull
    @NotEmpty
    @Length(min = 5, message = "Username should have at least 5 characters!")
    private String username;


    @Column(name = "password", unique = false, nullable = false)
    @NotNull
    @Length(min = 8, message = "Password should have at least 8 characters!")
    private String password;


    @Column(name = "email", unique = true, nullable = false)
    @NotNull
    @NotEmpty
    @Email(message = "Please provide a valid email address!")
    private String email;


    @Column(name = "activated", unique = false, nullable = false)
    private boolean activated;


    @Column(name = "defaultCurrency", unique = false, nullable = true)
    private Currency defaultCurrency;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user" , cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH})
    private Set<Category> categories = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user" , cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH})
    private Set<Income> incomes = new HashSet<>();


    @OneToOne (mappedBy="user", cascade = CascadeType.ALL)
    private ForgotPassword forgotPassword;

    @OneToOne (mappedBy="user", cascade = CascadeType.ALL)
    private UserRegistration userRegistration;


    public Set<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(Set<Income> incomes) {
        this.incomes = incomes;
    }

    public UserRegistration getUserRegistration() {
        return userRegistration;
    }

    public void setUserRegistration(UserRegistration userRegistration) {
        this.userRegistration = userRegistration;
    }

    public ForgotPassword getForgotPassword() {
        return forgotPassword;
    }

    public void setForgotPassword(ForgotPassword forgotPassword) {
        this.forgotPassword = forgotPassword;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public long getUserId() {

        return userId;
    }

    public void setUserId(long userId) {

        this.userId = userId;
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

    public boolean isActivated() {

        return activated;
    }

    public void setActivated(boolean activated) {

        this.activated = activated;
    }


    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(Currency defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}