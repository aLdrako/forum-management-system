package com.company.web.forummanagementsystem.helpers;

import com.company.web.forummanagementsystem.exceptions.AuthorizationException;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.service.UserServices;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Component
public class AuthenticationHelper {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication!";

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
