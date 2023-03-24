package com.telerikacademy.web.fms.models.dto;

import com.telerikacademy.web.fms.models.validations.*;
import jakarta.validation.constraints.*;

public class UserDTO {
    @NotEmpty(message = "First name can't be empty", groups = CreateValidationGroup.class)
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols",
            groups = {UpdateValidationGroup.class, CreateValidationGroup.class})
    private String firstName;
    @NotEmpty(message = "Last name can't be empty", groups = CreateValidationGroup.class)
    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32 symbols",
            groups = {UpdateValidationGroup.class, CreateValidationGroup.class})
    private String lastName;
    @NotEmpty(message = "Email can't be empty", groups = CreateValidationGroup.class)
    @Email(message = "Email has invalid format",
            groups = {UpdateValidationGroup.class, CreateValidationGroup.class})
    private String email;
    @NotEmpty(message = "Username can't be empty", groups = {CreateValidationGroup.class, LoginValidationGroup.class})
    @Size(min = 4, max = 16, message = "Username should be between 4 and 16 symbols",
            groups = {UpdateValidationGroup.class, CreateValidationGroup.class, LoginValidationGroup.class})
    @Null(message = "Username can't be changed",
            groups = UpdateValidationGroup.class)
    private String username;
    @NotEmpty (message = "Password can't be empty", groups = {CreateValidationGroup.class, LoginValidationGroup.class})
    @PasswordConstraint(min = 5, max = 20, groups = {UpdateValidationGroup.class, CreateValidationGroup.class, LoginValidationGroup.class})
    private String password;
    @Size(min = 7, max = 16, message = "Phone number should be between 7 and 16 symbols",
            groups = UpdateValidationGroup.class)
    private String phoneNumber;
//    @PhotoConstraint(groups = UpdateValidationGroup.class)
    private String photo;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
