package com.telerikacademy.web.fms.helpers;

import com.telerikacademy.web.fms.models.Permission;
import com.telerikacademy.web.fms.models.User;

import java.time.LocalDateTime;

public class Helpers {

    public static final String EMAIL_PREFIX = "email=";
    public static final String USERNAME_PREFIX = "username=";

    public static User createMockAdmin() {
        User mockAdminUser = createMockUser();
        mockAdminUser.getPermission().setAdmin(true);
        return mockAdminUser;
    }

    public static User createMockUser() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("MockFirstName");
        mockUser.setLastName("MockLastName");
        mockUser.setEmail("mock@mail.com");
        mockUser.setUsername("MockUsername");
        mockUser.setPassword("MockPassword");
        mockUser.setPhoneNumber(null);
        mockUser.setPhoto(null);
        mockUser.setPermission(new Permission());
        mockUser.setJoiningDate(LocalDateTime.now());
        return mockUser;
    }

    public static User createMockDifferentUser() {
        User mockDifferentUser = createMockUser();
        mockDifferentUser.setId(2L);
        mockDifferentUser.setUsername("MockDifferentUsername");
        mockDifferentUser.setEmail("mockdifferent@mail.com");
        return mockDifferentUser;
    }

}
