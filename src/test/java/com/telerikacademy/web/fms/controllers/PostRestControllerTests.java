package com.telerikacademy.web.fms.controllers;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.CommentOutputDTO;
import com.telerikacademy.web.fms.models.dto.PostDTO;
import com.telerikacademy.web.fms.models.dto.PostOutputDTO;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.services.contracts.CommentServices;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static com.telerikacademy.web.fms.helpers.Helpers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostRestControllerTests {
    @MockBean
    PostServices mockPostService;
    @MockBean
    CommentServices mockCommentService;
    @MockBean
    ModelMapper mockModelMapper;
    @MockBean
    AuthenticationHelper authenticationHelper;
    @Autowired
    MockMvc mockMvc;

    @Test
    public void getById_Should_ReturnStatusOk_When_PostExists() throws Exception {
        // Arrange

        PostOutputDTO postOutputDTO = createMockPostOutputDto();
        Mockito.when(mockModelMapper.objectToDto((Post) Mockito.any()))
                        .thenReturn(postOutputDTO);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(postOutputDTO.getTitle()));
    }
    @Test
    public void getById_Should_ReturnStatusNotFound_When_PostDoesNotExist() throws Exception {
        // Arrange
        Mockito.when(mockPostService.getById(Mockito.anyLong()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    public void create_Should_ReturnStatusOk_When_CorrectRequest() throws Exception {
        // Arrange
        User user = createMockUser();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(user);

        Post post = createMockPost();

        Mockito.when(mockModelMapper.dtoToObject((PostDTO) Mockito.any()))
               .thenReturn(post);

        PostOutputDTO postOutputDTO = createMockPostOutputDto();

        Mockito.when(mockModelMapper.objectToDto((Post) Mockito.any()))
                 .thenReturn(postOutputDTO);
        // Act, Assert
        String body = toJson(createPostDto());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void create_Should_ReturnStatusUnauthorized_When_AuthorizationMissing() throws Exception {
        // Arrange

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(AuthorizationException.class);

        // Act, Assert
        String body = toJson(createPostDto());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
    @Test
    public void create_Should_ReturnStatusBadRequest_When_BodyIsInvalid() throws Exception {
        // Arrange
        PostDTO postDTO = createPostDto();
        postDTO.setTitle(null);

        // Act, Assert
        String body = toJson(postDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void update_Should_ReturnStatusUnauthorized_When_AuthorizationMissing() throws Exception {
        // Arrange
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(AuthorizationException.class);
        // Act, Assert
        String body = toJson(createPostDto());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void update_Should_ReturnStatusBadRequest_When_BodyIsInvalid() throws Exception {
        // Arrange
        PostDTO postDTO = createPostDto();
        postDTO.setTitle(null);

        // Act, Assert
        String body = toJson(postDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    @Test
    public void update_Should_ReturnStatusNotFound_When_PostIsNotFound() throws Exception {
        // Arrange
        User user = createMockUser();
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(user);
        PostDTO postDTO = createPostDto();

        Mockito.when(mockModelMapper.dtoToObject(Mockito.anyLong(), (PostDTO) Mockito.any()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        String body = toJson(postDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    public void update_Should_ReturnStatusOk_When_CorrectRequest() throws Exception {
        // Arrange
        User user = createMockUser();
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(user);
        Post post = createMockPost();
        PostDTO postDTO = createPostDto();
        Mockito.when(mockModelMapper.dtoToObject(Mockito.anyLong(), (PostDTO) Mockito.any()))
                .thenReturn(post);
        PostOutputDTO postOutputDTO = createMockPostOutputDto();
        Mockito.when(mockModelMapper.objectToDto(post))
                .thenReturn(postOutputDTO);

        // Act, Assert
        String body = toJson(postDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    public void changePostLikes_Should_ReturnStatusNotFound_When_PostDoesNotExist() throws Exception {
        // Arrange
        Mockito.doThrow(EntityNotFoundException.class)
                        .when(mockPostService)
                                .changePostLikes(Mockito.anyLong(), Mockito.any());
        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}/like", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    public void changePostLikes_Should_ReturnStatusUnauthorized_When_AuthorizationMissing() throws Exception {
        // Arrange
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any()))
                        .thenThrow(AuthorizationException.class);
        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}/like", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void delete_Should_ReturnStatusUnauthorized_When_AuthorizationMissing() throws Exception {
        // Arrange
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(AuthorizationException.class);
        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
    @Test
    public void delete_Should_ReturnStatusNotFound_When_PostIsNotFound() throws Exception {
        // Arrange
        User user = createMockUser();
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(user);
        Mockito.doThrow(EntityNotFoundException.class)
                .when(mockPostService).delete(Mockito.anyLong(), Mockito.any());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    public void delete_Should_ReturnStatusOk_When_CorrectRequest() throws Exception {
        // Arrange
        User user = createMockUser();
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(user);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    public void getCommentsByPostId_Should_ReturnStatusNotFound_When_PostNotFound() throws Exception {
        // Arrange
        Mockito.when(mockCommentService.getCommentsByPostId(Mockito.anyLong(), Mockito.anyMap()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{postId}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    public void getCommentsByPostId_Should_ReturnStatusOk_When_PostIsFound() throws Exception {
        // Arrange
        Mockito.when(mockCommentService.getCommentsByPostId(Mockito.anyLong(), Mockito.anyMap()))
                .thenReturn(new ArrayList<>());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{postId}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    public void getCommentByPostId_Should_ReturnStatusNotFound_When_PostOrCommentDoesNotExist() throws Exception {
        // Arrange
        Mockito.when(mockCommentService.getCommentByPostId(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{postId}/comments/{commentId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    public void getCommentByPostId_Should_ReturnStatusOk_When_CorrectRequest() throws Exception {
        // Arrange
        CommentOutputDTO commentOutputDTO = createMockCommentOutputDTO();
        Mockito.when(mockModelMapper.objectToDto((Comment) Mockito.any()))
                        .thenReturn(commentOutputDTO);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{postId}/comments/{commentId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(commentOutputDTO.getContent()));
    }

}
