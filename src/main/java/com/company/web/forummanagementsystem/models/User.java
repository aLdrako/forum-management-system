package com.company.web.forummanagementsystem.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Optional;

public class User {

    private Long id;
    @NotEmpty(message = "First name can't be empty")
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols")
    private String firstName;
    @NotEmpty (message = "Last name can't be empty")
    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32 symbols")
    private String lastName;
    @Email (message = "Email has invalid format")
    private String email;
    @NotEmpty (message = "Username can't be empty")
    private String username;
    @NotEmpty (message = "Password can't be empty")
    private String password;
    private Optional<String> phoneNumber = Optional.empty();
    private boolean isAdmin;
    private boolean isBlocked;
    private final LocalDateTime joiningDate = LocalDateTime.now();

    public User() {
    }

    public User(Long id, String firstName, String lastName, String email, String username, String password, boolean isAdmin) {
        this(id, firstName, lastName, email, username, password);
        this.isAdmin = isAdmin;
    }

    public User(Long id, String firstName, String lastName, String email, String username, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Optional<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = Optional.ofNullable(phoneNumber);
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public LocalDateTime getJoiningDate() {
        return joiningDate;
    }

}
