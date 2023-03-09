package com.telerikacademy.web.fms.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.telerikacademy.web.fms.models.Permission;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.PermissionDTO;
import com.telerikacademy.web.fms.models.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.Optional;

public class Helpers {

    public static final String EMAIL_PREFIX = "email=";
    public static final String USERNAME_PREFIX = "username=";

    public static User createMockAdmin() {
        User mockAdminUser = createMockUser();
        mockAdminUser.setId(1L);
        mockAdminUser.getPermission().setAdmin(true);
        return mockAdminUser;
    }

    public static User createMockUser() {
        User mockUser = new User();
        mockUser.setId(2L);
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
        mockDifferentUser.setId(3L);
        mockDifferentUser.setUsername("MockDifferentUsername");
        mockDifferentUser.setEmail("mockdifferent@mail.com");
        return mockDifferentUser;
    }

    public static UserDTO createMockUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("MockFirstNameDTO");
        userDTO.setLastName("MockLastNameDTO");
        userDTO.setEmail("mockdto@mail.com");
        userDTO.setUsername("MockUsernameDTO");
        userDTO.setPassword("MockPasswordDTO");
        userDTO.setPhoneNumber(null);
        userDTO.setPhoto(null);
        return userDTO;
    }

    public static String toJson(final Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new Jdk8Module());
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
