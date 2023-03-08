package com.telerikacademy.web.fms.services;

import com.telerikacademy.web.fms.exceptions.DuplicateEntityException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static com.telerikacademy.web.fms.helpers.Helpers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServicesImplTests {

    @Mock
    UserRepository mockUserRepository;
    @InjectMocks
    UserServicesImpl userServices;

    @Test
    public void getAll_Should_CallRepository() {
        // Arrange
        when(mockUserRepository.getAll()).thenReturn(null);

        // Act
        userServices.getAll();

        // Assert
        verify(mockUserRepository).getAll();
    }

    @Test
    public void getById_Should_ReturnUser_When_MatchByIdExists() {
        // Arrange
        User mockUser = createMockUser();

        when(mockUserRepository.getById(anyLong())).thenReturn(mockUser);

        // Act
        User result = userServices.getById(mockUser.getId());

        // Assert
        Assertions.assertEquals(mockUser, result);
    }

    @Test
    public void search_Should_CallRepository() {
        // Arrange
        when(mockUserRepository.search(anyString())).thenReturn(null);

        // Act
        userServices.search(anyString());

        // Assert
        verify(mockUserRepository).search(anyString());
    }

    @Test
    public void create_Should_CallRepository_When_UserWithSameEmailOrUsernameDoesNotExits() {
        // Arrange
        User mockUser = createMockUser();

        when(mockUserRepository.search(EMAIL_PREFIX + mockUser.getEmail()))
                .thenThrow(EntityNotFoundException.class);

        when(mockUserRepository.search(USERNAME_PREFIX + mockUser.getUsername()))
                .thenThrow(EntityNotFoundException.class);

        // Act
        userServices.create(mockUser);

        // Assert
        verify(mockUserRepository).create(mockUser);
    }

    @Test
    public void create_Should_Throw_When_UserWithSameEmailExists() {
        // Arrange
        User mockUser = createMockUser();

        when(mockUserRepository.search(EMAIL_PREFIX + mockUser.getEmail()))
                .thenReturn(List.of(mockUser));

        // Act, Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> userServices.create(mockUser));
    }

    @Test
    public void create_Should_Throw_When_UserWithSameUsernameExists() {
        // Arrange
        User mockUser = createMockUser();

        lenient().when(mockUserRepository.search(USERNAME_PREFIX + mockUser.getUsername()))
                .thenReturn(List.of(mockUser));

        // Act, Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> userServices.create(mockUser));
    }

    @Test
    public void update_Should_CallRepository_When_UserIsTheOwnerOfAccount() {
        // Arrange
        User mockUser = createMockUser();

        when(mockUserRepository.getById(anyLong())).thenReturn(mockUser);

        // Act
        userServices.update(mockUser, mockUser);

        // Assert
        verify(mockUserRepository).update(mockUser);
    }

    @Test
    public void update_Should_CallRepository_When_UserIsAdmin() {
        // Arrange
        User mockUser = createMockUser();
        User mockAdminUser = createMockAdmin();

        when(mockUserRepository.getById(anyLong())).thenReturn(mockAdminUser);

        // Act
        userServices.update(mockUser, mockAdminUser);

        // Assert
        verify(mockUserRepository).update(mockUser);
    }

    @Test
    public void update_Should_ThrowException_When_UserIsNotTheOwnerOrAdmin() {
        // Arrange
        User mockUser = createMockUser();
        User mockDifferentUser = createMockDifferentUser();

        when(mockUserRepository.getById(anyLong())).thenReturn(mockUser);

        // Act, Arrange
        Assertions.assertThrows(
                UnauthorizedOperationException.class,
                () -> userServices.update(mockUser, mockDifferentUser)
        );
    }

    @Test
    public void update_Should_ThrowException_When_EmailIsTaken() {
        // Arrange
        User mockUser = createMockUser();
        User mockAdminUser = createMockAdmin();

        when(mockUserRepository.getById(anyLong())).thenReturn(mockUser);
        when(mockUserRepository.getById(anyLong())).thenReturn(mockAdminUser);

        mockUser.setEmail("mockdifferent@mail.com");

        // Act, Assert
        Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> userServices.update(mockUser, mockUser)
        );
    }

    @Test
    public void delete_Should_CallRepository_When_UserIsTheOwnerOfAccount() {
        // Arrange
        User mockDifferentUser = createMockDifferentUser();

        when(mockUserRepository.getById(anyLong())).thenReturn(mockDifferentUser);

        // Act
        userServices.delete(2L, mockDifferentUser);

        // Assert
        verify(mockUserRepository).delete(2L);
    }

    @Test
    public void delete_Should_CallRepository_When_UserIsAdmin() {
        // Arrange
        User mockDifferentUser = createMockDifferentUser();
        User mockAdminUser = createMockAdmin();

        when(mockUserRepository.getById(anyLong())).thenReturn(mockDifferentUser);

        // Act
        userServices.delete(2L, mockAdminUser);

        // Assert
        verify(mockUserRepository).delete(2L);
    }

    @Test
    public void delete_Should_ThrowException_WhenUserIsNotOwnerOrAdmin() {
        // Arrange
        User mockUser = createMockUser();
        User mockDifferentUser = createMockDifferentUser();

        when(mockUserRepository.getById(anyLong())).thenReturn(mockDifferentUser);

        // Act, Assert
        Assertions.assertThrows(
                UnauthorizedOperationException.class,
                () -> userServices.delete(2L, mockUser)
        );
    }

    @Test
    public void delete_Should_ThrowException_WhenDeletingSuperUser() {
        // Arrange
        User mockAdminUser = createMockAdmin();

        // Act, Assert
        Assertions.assertThrows(
                UnauthorizedOperationException.class,
                () -> userServices.delete(1L, mockAdminUser)
        );
    }

}
