package com.telerikacademy.web.fms.controllers;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityDuplicateException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.Permission;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.PermissionDTO;
import com.telerikacademy.web.fms.models.dto.UserDTO;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import static com.telerikacademy.web.fms.helpers.Helpers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserRestControllerTests {
    @MockBean
    UserServices mockUserServices;

    @MockBean
    ModelMapper mockModelMapper;

    @MockBean
    AuthenticationHelper mockAuthenticationHelper;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getAll_Should_ReturnStatusOk() throws Exception {
        // Arrange
        when(mockUserServices.getAll()).thenReturn(null);

        // Act, Arrange
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getById_Should_ReturnStatusOk_When_UserExists() throws Exception {
        // Arrange
        User mockUser = createMockUser();

        when(mockUserServices.getById(anyLong())).thenReturn(mockUser);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", 2))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(mockUser.getUsername()));
    }

    @Test
    public void getById_Should_ReturnStatusNotFound_When_UserDoesntExist() throws Exception {
        // Arrange
        when(mockUserServices.getById(anyLong())).thenThrow(EntityNotFoundException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", 2))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void create_Should_ReturnStatusOk_When_CorrectRequest() throws Exception {
        // Arrange
        User mockUser = createMockUser();

        when(mockModelMapper.dtoToObject((UserDTO) any())).thenReturn(mockUser);

        // Act, Assert
        String body = toJson(createMockUserDTO());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void create_Should_ReturnStatusConflict_When_UserWithSameEmailOrUsernameExists() throws Exception {
        // Arrange
        User mockUser = createMockUser();
        UserDTO mockUserDTO = createMockUserDTO();

        when(mockModelMapper.dtoToObject((UserDTO) any())).thenReturn(mockUser);
        doThrow(EntityDuplicateException.class)
                .when(mockUserServices).create(mockUser);

        // Act, Assert
        String body = toJson(mockUserDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void create_Should_ReturnStatusBadRequest_When_BodyIsInvalid() throws Exception {
        // Arrange
        UserDTO mockUserDTO = createMockUserDTO();
        mockUserDTO.setEmail(null);

        // Act, Arrange
        String body = toJson(mockUserDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void update_Should_ReturnStatusOk_When_CorrectRequest() throws Exception {
        // Arrange
        User mockUser = createMockUser();
        UserDTO mockUserDTO = createMockUserDTO();
        mockUserDTO.setUsername(null);

        when(mockAuthenticationHelper.tryGetUser(any())).thenReturn(mockUser);
        when(mockModelMapper.dtoToObject((UserDTO) any())).thenReturn(mockUser);

        // Act, Assert
        String body = toJson(mockUserDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void update_Should_ReturnStatusUnauthorized_When_AuthorizationIsMissing() throws Exception {
        // Arrange
        UserDTO mockUserDTO = createMockUserDTO();
        mockUserDTO.setUsername(null);

        when(mockAuthenticationHelper.tryGetUser(any()))
                .thenThrow(new AuthorizationException(null));

        //Act, Assert
        String body = toJson(mockUserDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void update_Should_ReturnStatusBadRequest_When_BodyIsInvalid() throws Exception {
        // Arrange
        UserDTO mockUserDTO = createMockUserDTO();
        mockUserDTO.setUsername(null);
        mockUserDTO.setEmail("mail");

        // Act, Assert
        String body = toJson(mockUserDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void update_Should_ReturnStatusConflict_When_UserWithSameEmailExists() throws Exception {
        // Arrange
        User mockUser = createMockUser();
        UserDTO mockUserDTO = createMockUserDTO();
        mockUserDTO.setUsername(null);

        when(mockAuthenticationHelper.tryGetUser(any())).thenReturn(mockUser);
        when(mockModelMapper.dtoToObject(anyLong(), (UserDTO) any())).thenReturn(mockUser);

        doThrow(EntityDuplicateException.class)
                .when(mockUserServices).update(mockUser, mockUser);

        // Act, Arrange
        String body = toJson(mockUserDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void update_Should_ReturnStatusUnauthorized_When_UserIsNotAuthorizedToEdit() throws Exception {
        // Arrange
        User mockUser = createMockUser();
        UserDTO mockUserDTO = createMockUserDTO();
        mockUserDTO.setUsername(null);

        when(mockAuthenticationHelper.tryGetUser(Mockito.any())).thenReturn(mockUser);

        when(mockModelMapper.dtoToObject(anyLong(), (UserDTO) any())).thenReturn(mockUser);

        doThrow(AuthorizationException.class).when(mockUserServices).update(mockUser, mockUser);

        // Act, Assert
        String body = toJson(mockUserDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void updatePermissions_Should_ReturnStatusOk_When_CorrectRequest() throws Exception {
        // Arrange
        User mockAdminUser = createMockAdmin();
        Permission permission = new Permission(2L);

        when(mockAuthenticationHelper.tryGetUser(any())).thenReturn(mockAdminUser);

        when(mockModelMapper.dtoToObject(anyLong(), (PermissionDTO) any())).thenReturn(permission);

        when(mockUserServices.updatePermissions(permission, mockAdminUser)).thenReturn(null);

        // Act, Arrange
        String body = toJson(new PermissionDTO());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}/permissions", 2)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updatePermissions_Should_ReturnStatusUnauthorized_When_UserIsNotAdmin() throws Exception {
        // Arrange
        User mockUser = createMockUser();
        Permission permission = new Permission(2L);

        when(mockAuthenticationHelper.tryGetUser(any())).thenReturn(mockUser);

        when(mockModelMapper.dtoToObject(anyLong(), (PermissionDTO) any())).thenReturn(permission);

        doThrow(UnauthorizedOperationException.class).when(mockUserServices).updatePermissions(permission, mockUser);

        // Act, Arrange
        String body = toJson(new PermissionDTO());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}/permissions", 2)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void updatePermissions_Should_ReturnStatusNotFound_When_UserDoesNotExist() throws Exception {
        // Arrange
        User mockUser = createMockUser();
        Permission permission = new Permission(2L);

        when(mockAuthenticationHelper.tryGetUser(any())).thenReturn(mockUser);

        when(mockModelMapper.dtoToObject(anyLong(), (PermissionDTO) any())).thenThrow(EntityNotFoundException.class);

        // Act, Assert
        String body = toJson(new PermissionDTO());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}/permissions", 2)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void delete_Should_ReturnStatusOk_When_CorrectRequest() throws Exception {
        // Arrange
        User mockUser = createMockUser();

        when(mockAuthenticationHelper.tryGetUser(any())).thenReturn(mockUser);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", 2))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void delete_Should_ReturnStatusUnauthorized_When_AuthorizationIsMissing() throws Exception {
        // Arrange
        when(mockAuthenticationHelper.tryGetUser(any())).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, null));

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", 2))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void delete_Should_ReturnStatusNotFound_When_UserDoesNotExist() throws Exception {
        // Arrange
        User mockUser = createMockUser();

        when(mockAuthenticationHelper.tryGetUser(any())).thenReturn(mockUser);

        doThrow(EntityNotFoundException.class).when(mockUserServices).delete(2L, mockUser);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", 2))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void delete_Should_ReturnStatusUnauthorized_When_UserIsNotAuthorizedToEdit() throws Exception {
        // Arrange
        User mockUser = createMockUser();

        when(mockAuthenticationHelper.tryGetUser(any())).thenReturn(mockUser);
        doThrow(AuthorizationException.class).when(mockUserServices).delete(2L, mockUser);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", 2))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}
