package com.telerikacademy.web.fms.services;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.repositories.contracts.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.telerikacademy.web.fms.helpers.Helpers.createMockTag;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TagServicesImplTests {
    @Mock
    TagRepository mockTagRepository;
    @InjectMocks
    TagServicesImpl services;

    @Test
    public void getTagById_Should_CallRepository() {
        // Arrange
        Tag tag = createMockTag();

        Mockito.when(mockTagRepository.getTagById(tag.getId()))
                .thenReturn(tag);
        // Act
        services.getTagById(tag.getId());
        // Assert
        Mockito.verify(mockTagRepository, times(1))
                .getTagById(tag.getId());
    }

    @Test
    public void getTagById_Should_ThrowException_When_TagIdDoesNotExist() {
        // Arrange
        Tag tag = createMockTag();

        Mockito.when(mockTagRepository.getTagById(tag.getId()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> services.getTagById(tag.getId()));
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
}
