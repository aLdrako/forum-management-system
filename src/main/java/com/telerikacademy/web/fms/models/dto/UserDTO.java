package com.telerikacademy.web.fms.models.dto;

import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import jakarta.validation.constraints.*;

import java.util.Optional;

public class UserDTO {
    @NotEmpty(message = "First name can't be empty")
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols",
            groups = UpdateValidationGroup.class)
    private String firstName;
    @NotEmpty(message = "Last name can't be empty")
    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32 symbols",
            groups = UpdateValidationGroup.class)
    private String lastName;
    @NotEmpty(message = "Email can't be empty")
    @Email(message = "Email has invalid format",
            groups = UpdateValidationGroup.class)
    private String email;
    @NotEmpty(message = "Username can't be empty")
    @Size(min = 4, max = 16, message = "Username should be between 4 and 16 symbols")
    @Null(message = "Username can't be changed",
            groups = UpdateValidationGroup.class)
    private String username;
    @NotEmpty (message = "Password can't be empty")
    @Size(min = 6, max = 20, message = "Password should be between 6 and 20 symbols",
            groups = UpdateValidationGroup.class)
    private String password;
    @Size(min = 7, max = 16, message = "Phone number should be between 7 and 16 symbols",
            groups = UpdateValidationGroup.class)
    private String phoneNumber;
    private byte[] photo;

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
        return Optional.ofNullable(phoneNumber);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Optional<byte[]> getPhoto() {
        return Optional.ofNullable(photo);
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
