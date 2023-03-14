package com.telerikacademy.web.fms.services;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.PostRepository;
import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
import com.telerikacademy.web.fms.services.contracts.TagServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.telerikacademy.web.fms.helpers.Helpers.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PostServicesImplTests {
    @Mock
    PostRepository mockPostRepository;
    @Mock
    UserRepository mockUserRepository;
    @Mock
    TagServices mockTagService;

    @InjectMocks
    PostServicesImpl services;

    @Test
    public void getById_Should_CallRepository() {
        // Arrange
        Post mockPost = createMockPost();

        Mockito.when(mockPostRepository.getById(Mockito.anyLong()))
                .thenReturn(mockPost);
        // Act
        services.getById(mockPost.getId());
        // Assert
        Mockito.verify(mockPostRepository, times(1))
                .getById(Mockito.anyLong());
    }

    @Test
    public void getById_Should_ReturnPost_When_MatchingByIdExist() {
        // Arrange
        Post mockPost = createMockPost();

        Mockito.when(mockPostRepository.getById(mockPost.getId()))
                .thenReturn(mockPost);

        // Act, Assert
        Assertions.assertEquals(mockPost, services.getById(mockPost.getId()));
    }

    @Test
    public void getById_Should_ThrowException_When_IdDoesNotExist() {
        // Arrange
        Post mockPost = createMockPost();

        Mockito.when(mockPostRepository.getById(mockPost.getId()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> services.getById(mockPost.getId()));
    }

    @Test
    public void getAll_Should_CallRepository() {
        // Arrange
        Mockito.when(mockPostRepository.getAll(Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty()))
                .thenReturn(null);

        // Act
        services.getAll(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());
        // Assert
        Mockito.verify(mockPostRepository, times(1))
                .getAll(Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty());
    }

    @Test
    public void create_Should_CallRepository() {
        // Arrange
        Post post = createMockPost();
        User userCreated = post.getUserCreated();

        // Act
        services.create(post, userCreated);

        // Assert
        Mockito.verify(mockPostRepository, times(1))
                .create(post);
    }

    @Test
    public void create_Should_ReturnPost() {
        // Arrange
        Post post = createMockPost();
        User userCreated = post.getUserCreated();

        Mockito.when(mockPostRepository.create(post))
                .thenReturn(post);

        // Act, Assert
        Assertions.assertEquals(post, services.create(post, userCreated));
    }

    @Test
    public void create_Should_ThrowException_When_UserIsNotCreator() {
        // Arrange
        Post post = createMockPost();
        User userCreated = createMockUser();
        userCreated.setId(5L);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () ->
                services.create(post, userCreated));
    }

    @Test
    public void create_Should_ThrowException_When_UserIsBlocked() {
        // Arrange
        Post post = createMockPost();
        User userCreated = createMockUser();
        userCreated.getPermission().setBlocked(true);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () ->
                services.create(post, userCreated));
    }

    @Test
    public void create_Should_ReturnPost_When_UserIsAdmin() {
        // Arrange
        Post post = createMockPost();
        User userCreated = createMockAdmin();

        Mockito.when(mockPostRepository.create(post))
                .thenReturn(post);

        // Act, Assert
        Assertions.assertEquals(post, services.create(post, userCreated));
    }

    @Test
    public void delete_Should_CallRepository() {
        // Arrange
        Post post = createMockPost();

        Mockito.when(mockPostRepository.getById(post.getId()))
                .thenReturn(post);
        // Act
        services.delete(post.getId(), post.getUserCreated());

        // Assert
        Mockito.verify(mockPostRepository, times(1))
                .delete(post);
    }

    @Test
    public void delete_Should_ThrowException_When_IdDoesNotExist() {
        // Arrange
        Post post = createMockPost();

        Mockito.when(mockPostRepository.getById(post.getId()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                services.delete(post.getId(), post.getUserCreated()));
    }

    @Test
    public void delete_Should_ThrowException_When_UserIsNotCreator() {
        // Arrange
        Post post = createMockPost();
        User user = createMockUser();
        user.setId(5L);

        Mockito.when(mockPostRepository.getById(post.getId()))
                .thenReturn(post);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () ->
                services.delete(post.getId(), user));
    }

    @Test
    public void delete_Should_ThrowException_When_UserIsBlocked() {
        // Arrange
        Post post = createMockPost();
        User user = post.getUserCreated();
        user.getPermission().setBlocked(true);

        Mockito.when(mockPostRepository.getById(post.getId()))
                .thenReturn(post);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () ->
                services.delete(post.getId(), user));
    }

    @Test
    public void delete_Should_CallRepository_When_UserIsAdmin() {
        // Arrange
        Post post = createMockPost();
        User user = createMockAdmin();

        Mockito.when(mockPostRepository.getById(post.getId()))
                .thenReturn(post);

        // Act
        services.delete(post.getId(), user);
        // Assert
        Mockito.verify(mockPostRepository, times(1))
                .delete(post);
    }

    @Test
    public void update_Should_CallRepository() {
        // Arrange
        Post post = createMockPost();
        User userCreated = post.getUserCreated();

        // Act
        services.update(post, userCreated);

        // Assert
        Mockito.verify(mockPostRepository, times(1))
                .update(post);
    }

    @Test
    public void update_Should_ReturnPost() {
        // Arrange
        Post post = createMockPost();
        User userCreated = post.getUserCreated();

        Mockito.when(mockPostRepository.update(post))
                .thenReturn(post);

        // Act, Assert
        Assertions.assertEquals(post, services.update(post, userCreated));
    }

    @Test
    public void update_Should_ThrowException_When_UserIsNotCreator() {
        // Arrange
        Post post = createMockPost();
        User userCreated = createMockUser();
        userCreated.setId(5L);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () ->
                services.update(post, userCreated));
    }

    @Test
    public void update_Should_ThrowException_When_UserIsBlocked() {
        // Arrange
        Post post = createMockPost();
        User userCreated = createMockUser();
        userCreated.getPermission().setBlocked(true);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () ->
                services.update(post, userCreated));
    }

    @Test
    public void update_Should_ReturnPost_When_UserIsAdmin() {
        // Arrange
        Post post = createMockPost();
        User userCreated = createMockAdmin();

        Mockito.when(mockPostRepository.update(post))
                .thenReturn(post);

        // Act, Assert
        Assertions.assertEquals(post, services.update(post, userCreated));
    }

    @Test
    public void getPostByUserId_Should_CallRepository() {
        // Arrange
        Post post = createMockPost();
        User user = post.getUserCreated();

        Mockito.when(mockUserRepository.getById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito.when(mockPostRepository.getPostByUserId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(post);
        // Act
        services.getPostByUserId(user.getId(), post.getId());
        // Assert
        Mockito.verify(mockPostRepository, times(1))
                .getPostByUserId(user.getId(), post.getId());
    }

    @Test
    public void getPostByUserId_Should_ThrowException_When_UserNotFound() {
        // Arrange
        Post post = createMockPost();
        User user = post.getUserCreated();

        Mockito.when(mockUserRepository.getById(user.getId()))
                .thenThrow(EntityNotFoundException.class);

        // Assert, Act
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                services.getPostByUserId(user.getId(), post.getId()));
    }
    @Test
    public void getPostByUserId_Should_ThrowException_When_PostIsNotFound() {
        // Arrange
        Post post = createMockPost();
        User user = post.getUserCreated();

        Mockito.when(mockUserRepository.getById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito.when(mockPostRepository.getPostByUserId(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(EntityNotFoundException.class);

        // Assert, Act
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                services.getPostByUserId(user.getId(), post.getId()));
    }

    @Test
    public void changePostLikes_Should_CallRepository() {
        // Arrange
        Post post = createMockPost();
        User user = createMockUser();
        user.getPermission().setBlocked(false);

        Mockito.when(mockPostRepository.getById(post.getId()))
                .thenReturn(post);
        // Act
        services.changePostLikes(post.getId(), user);
        // Assert
        Mockito.verify(mockPostRepository, times(1))
                .update(post);
    }

    @Test
    public void changePostLikes_Should_ThrowException_When_UserIsBLocked() {
        // Assert
        Post post = createMockPost();
        User user = createMockUser();
        user.getPermission().setBlocked(true);

        Mockito.when(mockPostRepository.getById(post.getId()))
                .thenReturn(post);
        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> services.changePostLikes(post.getId(), user));
    }
    @Test
    public void changePostLikes_Should_AddLike() {
        // Assert
        Post post = createMockPost();
        User user = createMockUser();
        user.getPermission().setBlocked(false);

        Mockito.when(mockPostRepository.getById(post.getId()))
                .thenReturn(post);

        Mockito.when(mockPostRepository.update(post))
                .thenReturn(post);
        // Act
        services.changePostLikes(post.getId(), user);
        // Assert
        Assertions.assertTrue(post.getLikes().contains(user));
    }
    @Test
    public void changePostLikes_Should_RemoveLike() {
        // Assert
        Post post = createMockPost();
        User user = createMockUser();
        user.getPermission().setBlocked(false);
        post.addLike(user);

        Mockito.when(mockPostRepository.getById(post.getId()))
                .thenReturn(post);

        Mockito.when(mockPostRepository.update(post))
                .thenReturn(post);
        // Act
        services.changePostLikes(post.getId(), user);
        // Assert
        Assertions.assertFalse(post.getLikes().contains(user));
    }

    @Test
    public void updateTagsInPost_Should_AddTag() {
        // Arrange
        Post post = createMockPost();
        Tag tag = createMockTag();

        Mockito.when(mockTagService.createTag(tag.getName()))
                .thenReturn(tag);

        Mockito.when(mockPostRepository.update(post))
                .thenReturn(post);
        // Act
        services.updateTagsInPost(List.of(tag.getName()), post);
        // Assert
        Assertions.assertTrue(post.getTags().contains(tag));
    }
    @Test
    public void updateTagsInPost_Should_RemoveTag() {
        // Arrange
        Post post = createMockPost();
        Tag tag = createMockTag();
        post.addTag(tag);

        Mockito.when(mockTagService.createTag(tag.getName()))
                .thenReturn(tag);

        Mockito.when(mockPostRepository.update(post))
                .thenReturn(post);
        // Act
        services.updateTagsInPost(List.of(tag.getName()), post);
        // Assert
        Assertions.assertFalse(post.getTags().contains(tag));
    }

    @Test
    public void search_Should_CallRepository() {
        // Arrange
        Mockito.when(mockPostRepository.search(Optional.empty()))
                .thenReturn(null);

        // Act
        services.search(Optional.empty());
        // Assert
        Mockito.verify(mockPostRepository, times(1))
                .search(Optional.empty());
    }
}
