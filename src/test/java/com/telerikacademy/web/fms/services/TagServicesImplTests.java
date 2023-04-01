package com.telerikacademy.web.fms.services;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.telerikacademy.web.fms.helpers.Helpers.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TagServicesImplTests {
    @Mock
    TagRepository mockTagRepository;
    @InjectMocks
    TagServicesImpl services;

    @Test
    public void getAll_Should_CallRepository_When_UserIsAdmin() {
        // Arrange
        User user = createMockAdmin();
        // Act
        services.getAll(user);
        // Assert
        Mockito.verify(mockTagRepository, times(1))
                .getAll();
    }
    @Test
    public void getAll_Should_ThrowException_When_UserIsNotAnAdmin() {
        // Arrange
        User user = createMockUser();
        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> services.getAll(user));
    }
    @Test
    public void getTagById_Should_CallRepository() {
        // Arrange
        Tag tag = createMockTag();
        User user = createMockAdmin();

        Mockito.when(mockTagRepository.getTagById(tag.getId()))
                .thenReturn(tag);
        // Act
        services.getTagById(tag.getId(), user);
        // Assert
        Mockito.verify(mockTagRepository, times(1))
                .getTagById(tag.getId());
    }

    @Test
    public void getTagById_Should_ThrowException_When_TagIdDoesNotExist() {
        // Arrange
        Tag tag = createMockTag();
        User user = createMockAdmin();

        Mockito.when(mockTagRepository.getTagById(tag.getId()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> services.getTagById(tag.getId(), user));
    }

    @Test
    public void getTagById_Should_ThrowException_When_UserIsNotAnAdmin() {
        // Arrange
        Tag tag = createMockTag();
        User user = createMockUser();


        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> services.getTagById(tag.getId(), user));
    }

    @Test
    public void createTag_Should_CallRepository() {
        // Arrange
        Tag tag = createMockTag();

        Mockito.when(mockTagRepository.getTagByName(tag.getName()))
                .thenThrow(EntityNotFoundException.class);
        // Act
        services.createTag(tag.getName());
        // Assert
        Mockito.verify(mockTagRepository, times(1))
                .createTag(tag);
    }

    @Test
    public void createTag_Should_ReturnTagWhenAlreadyExists() {
        // Arrange
        Tag tag = createMockTag();

        Mockito.when(mockTagRepository.getTagByName(tag.getName()))
                .thenReturn(tag);

        // Act, Assert
        Assertions.assertEquals(services.createTag(tag.getName()), tag);
    }
    @Test
    public void delete_Should_CallRepository() {
        // Arrange
        Tag tag = createMockTag();
        User user = createMockAdmin();

        Mockito.when(mockTagRepository.getTagById(tag.getId()))
                .thenReturn(tag);

        // Act
        services.delete(tag.getId(), user);
        // Assert
        Mockito.verify(mockTagRepository, times(1))
                .delete(tag);
    }
    @Test
    public void delete_Should_ThrowException_When_TagDoesNotExist() {
        // Arrange
        Tag tag = createMockTag();
        User user = createMockAdmin();

        Mockito.when(mockTagRepository.getTagById(tag.getId()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> services.delete(tag.getId(), user));
    }
    @Test
    public void delete_Should_ThrowException_When_UserIsNotAnAdmin() {
        // Arrange
        Tag tag = createMockTag();
        User user = createMockUser();

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> services.delete(tag.getId(), user));
    }
}
