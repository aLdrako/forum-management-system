package com.telerikacademy.web.fms.services;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.CommentRepository;
import com.telerikacademy.web.fms.repositories.contracts.PostRepository;
import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.telerikacademy.web.fms.helpers.Helpers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServicesImplTests {

    @Mock
    UserRepository mockUserRepository;
    @Mock
    PostRepository mockPostRepository;
    @Mock
    CommentRepository mockCommentRepository;
    @InjectMocks
    CommentServicesImpl commentServices;

    @Test
    public void getAll_Should_CallRepository() {
        // Arrange
        when(mockCommentRepository.getAll()).thenReturn(null);

        // Act
        commentServices.getAll();

        // Assert
        verify(mockCommentRepository).getAll();
    }

    @Test
    public void getById_Should_ReturnComment_When_MatchByIdExists() {
        // Arrange
        Comment mockComment = createMockComment();

        when(mockCommentRepository.getById(anyLong())).thenReturn(mockComment);

        // Act
        Comment result = commentServices.getById(mockComment.getId());

        // Assert
        Assertions.assertEquals(mockComment, result);
    }

    @Test
    public void create_Should_CallRepository_When_UserIsAdmin() {
        // Arrange
        User mockAdminUser = createMockAdmin();
        Comment mockComment = createMockComment();
        mockComment.setCreatedBy(createMockDifferentUser());

        when(mockCommentRepository.create(mockComment)).thenReturn(mockComment);

        // Act
        commentServices.create(mockComment, mockAdminUser);

        // Assert
        verify(mockCommentRepository).create(mockComment);
    }

    @Test
    public void create_Should_CallRepository_When_UserIsTheCreatorOfComment() {
        // Arrange
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();

        when(mockCommentRepository.create(mockComment)).thenReturn(mockComment);

        // Act
        commentServices.create(mockComment, mockUser);

        // Assert
        verify(mockCommentRepository).create(mockComment);
    }

    @Test
    public void create_Should_ThrowException_When_UserIsNotCreatorOfComment() {
        // Arrange
        User mockDifferentUser = createMockDifferentUser();
        Comment mockComment = createMockComment();

        // Act, Assert
        Assertions.assertThrows(
                UnauthorizedOperationException.class,
                () -> commentServices.create(mockComment, mockDifferentUser)
        );
    }

    @Test
    public void create_Should_ThrowException_When_UserIsBlocked() {
        // Arrange
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();

        mockUser.getPermission().setBlocked(true);

        // Act, Assert
        Assertions.assertThrows(
                UnauthorizedOperationException.class,
                () -> commentServices.create(mockComment, mockUser)
        );
    }

    @Test
    public void update_Should_CallRepository_When_UserIsAdmin() {
        // Arrange
        User mockAdminUser = createMockAdmin();
        Comment mockComment = createMockComment();
        mockComment.setCreatedBy(createMockDifferentUser());

        when(mockCommentRepository.update(mockComment)).thenReturn(mockComment);

        // Act
        commentServices.update(mockComment, mockAdminUser);

        // Assert
        verify(mockCommentRepository).update(mockComment);
    }

    @Test
    public void update_Should_CallRepository_When_UserIsTheCreatorOfComment() {
        // Arrange
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();

        when(mockCommentRepository.update(mockComment)).thenReturn(mockComment);

        // Act
        commentServices.update(mockComment, mockUser);

        // Assert
        verify(mockCommentRepository).update(mockComment);
    }

    @Test
    public void update_Should_ThrowException_When_UserIsNotCreatorOfComment() {
        // Arrange
        User mockDifferentUser = createMockDifferentUser();
        Comment mockComment = createMockComment();

        // Act, Assert
        Assertions.assertThrows(
                UnauthorizedOperationException.class,
                () -> commentServices.update(mockComment, mockDifferentUser)
        );
    }

    @Test
    public void update_Should_ThrowException_When_UserIsBlocked() {
        // Arrange
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();

        mockUser.getPermission().setBlocked(true);

        // Act, Assert
        Assertions.assertThrows(
                UnauthorizedOperationException.class,
                () -> commentServices.update(mockComment, mockUser)
        );
    }

    @Test
    public void delete_Should_CallRepository_When_UserIsAdmin() {
        // Arrange
        User mockAdminUser = createMockAdmin();
        Comment mockComment = createMockComment();
        mockComment.setCreatedBy(createMockDifferentUser());

        when(mockCommentRepository.getById(anyLong())).thenReturn(mockComment);
        doNothing().when(mockCommentRepository).delete(anyLong());

        // Act
        commentServices.delete(mockComment.getId(), mockAdminUser);

        // Assert
        verify(mockCommentRepository).delete(mockComment.getId());
    }

    @Test
    public void delete_Should_CallRepository_When_UserIsTheCreatorOfComment() {
        // Arrange
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();

        when(mockCommentRepository.getById(anyLong())).thenReturn(mockComment);
        doNothing().when(mockCommentRepository).delete(anyLong());

        // Act
        commentServices.delete(mockComment.getId(), mockUser);

        // Assert
        verify(mockCommentRepository).delete(mockComment.getId());
    }

    @Test
    public void delete_Should_ThrowException_When_CommentNotFound() {
        // Arrange
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();

        // Act
        when(mockCommentRepository.getById(anyLong())).thenThrow(EntityNotFoundException.class);

        // Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> commentServices.delete(mockComment.getId(), mockUser)
        );
    }

    @Test
    public void delete_Should_ThrowException_When_UserIsNotCreatorOfComment() {
        // Arrange
        User mockDifferentUser = createMockDifferentUser();
        Comment mockComment = createMockComment();

        when(mockCommentRepository.getById(anyLong())).thenReturn(mockComment);

        // Act, Assert
        Assertions.assertThrows(
                UnauthorizedOperationException.class,
                () -> commentServices.delete(mockComment.getId(), mockDifferentUser)
        );
    }

    @Test
    public void delete_Should_ThrowException_When_UserIsBlocked() {
        // Arrange
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();
        mockUser.getPermission().setBlocked(true);

        when(mockCommentRepository.getById(anyLong())).thenReturn(mockComment);

        // Act, Assert
        Assertions.assertThrows(
                UnauthorizedOperationException.class,
                () -> commentServices.delete(mockComment.getId(), mockUser)
        );
    }

    @Test
    public void getCommentByUserId_Should_CallRepository() {
        // Arrange
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();

        when(mockUserRepository.getById(anyLong())).thenReturn(mockUser);
        when(mockCommentRepository.getCommentByUserId(mockUser.getId(), mockComment.getId())).thenReturn(mockComment);

        // Act
        commentServices.getCommentByUserId(mockUser.getId(), mockComment.getId());

        // Assert
        verify(mockCommentRepository).getCommentByUserId(mockUser.getId(), mockComment.getId());
    }

    @Test
    public void getCommentByUserId_Should_ThrowException_When_UserNotFound() {
        // Arrange
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();

        // Act
        when(mockUserRepository.getById(anyLong())).thenThrow(EntityNotFoundException.class);

        // Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> commentServices.getCommentByUserId(mockUser.getId(), mockComment.getId())
        );
    }

    @Test
    public void getCommentsByUserId_Should_CallRepository() {
        // Arrange
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();
        Map<String, String> parameters = new HashMap<>();

        when(mockUserRepository.getById(anyLong())).thenReturn(mockUser);
        when(mockCommentRepository.getCommentsByUserId(mockUser.getId(), parameters)).thenReturn(List.of(mockComment));

        // Act
        commentServices.getCommentsByUserId(mockUser.getId(), parameters);

        // Assert
        verify(mockCommentRepository).getCommentsByUserId(mockUser.getId(), parameters);
    }

    @Test
    public void getCommentsByUserId_Should_ThrowException_When_UserNotFound() {
        // Arrange
        User mockUser = createMockUser();
        Map<String, String> parameters = new HashMap<>();

        // Act
        when(mockUserRepository.getById(anyLong())).thenThrow(EntityNotFoundException.class);

        // Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> commentServices.getCommentsByUserId(mockUser.getId(), parameters));
    }

    @Test
    public void getCommentByPostId_Should_CallRepository() {
        // Arrange
        Post mockPost = createMockPost();
        Comment mockComment = createMockComment();

        when(mockPostRepository.getById(anyLong())).thenReturn(mockPost);
        when(mockCommentRepository.getCommentByPostId(mockPost.getId(), mockComment.getId())).thenReturn(mockComment);

        // Act
        commentServices.getCommentByPostId(mockPost.getId(), mockComment.getId());

        // Assert
        verify(mockCommentRepository).getCommentByPostId(mockPost.getId(), mockComment.getId());
    }

    @Test
    public void getCommentByPostId_Should_ThrowException_When_PostNotFound() {
        // Arrange
        Post mockPost = createMockPost();
        Comment mockComment = createMockComment();

        // Act
        when(mockPostRepository.getById(anyLong())).thenThrow(EntityNotFoundException.class);

        // Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> commentServices.getCommentByPostId(mockPost.getId(), mockComment.getId())
        );
    }

    @Test
    public void getCommentsByPostId_Should_CallRepository() {
        // Arrange
        Post mockPost = createMockPost();
        Comment mockComment = createMockComment();
        Map<String, String> parameters = new HashMap<>();

        when(mockPostRepository.getById(anyLong())).thenReturn(mockPost);
        when(mockCommentRepository.getCommentsByPostId(mockPost.getId(), parameters)).thenReturn(List.of(mockComment));

        // Act
        commentServices.getCommentsByPostId(mockPost.getId(), parameters);

        // Assert
        verify(mockCommentRepository).getCommentsByPostId(mockPost.getId(), parameters);
    }

    @Test
    public void getCommentsByPostId_Should_ThrowException_When_PostNotFound() {
        // Arrange
        Post mockPost = createMockPost();
        Map<String, String> parameters = new HashMap<>();

        // Act
        when(mockPostRepository.getById(anyLong())).thenThrow(EntityNotFoundException.class);

        // Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> commentServices.getCommentsByPostId(mockPost.getId(), parameters));
    }
}
