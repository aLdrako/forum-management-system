package com.telerikacademy.web.fms.helpers;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Component
public class AuthenticationHelper {
    private static final String USERNAME_PREFIX = "username=";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication!";
    private static final String INVALID_AUTHORIZATION_ERROR = "Unauthorized operation";
    private final UserServices userServices;

    public AuthenticationHelper(UserServices userServices) {
        this.userServices = userServices;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
        try {
            Optional<String> userInfo = Objects.requireNonNull(headers.getFirst(AUTHORIZATION_HEADER_NAME)).describeConstable();
            String[] credentials = validateHeaderValues(userInfo.orElse(""));

            User user = userServices.search(String.valueOf(credentials[0])).get(0);
            if (!user.getPassword().equals(credentials[1])) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }
            return user;
        } catch (AuthorizationException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
    }

    public User verifyAuthentication(String username, String password) {
        try {
            User user = userServices.search(USERNAME_PREFIX + username).get(0);
            if (!user.getPassword().equals(password)) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
    }

    public User tryGetCurrentUser(HttpSession session) {
        String currentUsername = (String) session.getAttribute("currentUser");

        if (currentUsername == null) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userServices.search(USERNAME_PREFIX + currentUsername).get(0);
    }

    public void tryGetCurrentAdmin(HttpSession session) {
        String currentUsername = (String) session.getAttribute("currentUser");
        if (currentUsername == null) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
        User user = userServices.search(USERNAME_PREFIX + currentUsername).get(0);
        if (!user.getPermission().isAdmin()) {
            throw new UnsupportedOperationException(INVALID_AUTHORIZATION_ERROR);
        }
    }

    public String[] validateHeaderValues(String headerValue) {
        String[] credentials = headerValue.split("[, ]+", -1);
        if (!headerValue.contains(",") && !headerValue.contains(" ")
                || credentials[0].strip().length() == 0 || credentials[1].strip().length() == 0) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
        credentials[0] = String.valueOf(Collections.singletonMap("username", credentials[0].strip())
                .entrySet().iterator().next());
        credentials[1] = credentials[1].strip();
        return credentials;
    }

}
