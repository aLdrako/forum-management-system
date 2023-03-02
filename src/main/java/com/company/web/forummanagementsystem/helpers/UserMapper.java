package com.company.web.forummanagementsystem.helpers;

import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.models.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User dtoToObject(Long id, UserDTO userDTO) {
        return dtoToObject(new User(id), userDTO);
    }

    public User dtoToObject(User user, UserDTO userDTO) {
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber().orElse(null));
        return user;
    }
}
