package com.telerikacademy.web.fms.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.telerikacademy.web.fms.models.*;
import com.telerikacademy.web.fms.models.dto.CommentOutputDTO;
import com.telerikacademy.web.fms.models.dto.PostDTO;
import com.telerikacademy.web.fms.models.dto.PostOutputDTO;
import com.telerikacademy.web.fms.models.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

import static com.telerikacademy.web.fms.helpers.DateTimeFormatHelper.formatToString;

public class Helpers {

    public static final String EMAIL_PREFIX = "email=";
    public static final String USERNAME_PREFIX = "username=";

    public static User createMockAdmin() {
        User mockAdminUser = createMockUser();
        mockAdminUser.setId(1L);
        mockAdminUser.getPermission().setAdmin(true);
        return mockAdminUser;
    }

    public static User createMockUser() {
        User mockUser = new User();
        mockUser.setId(2L);
        mockUser.setFirstName("MockFirstName");
        mockUser.setLastName("MockLastName");
        mockUser.setEmail("mock@mail.com");
        mockUser.setUsername("MockUsername");
        mockUser.setPassword("MockPassword");
        mockUser.setPhoneNumber(null);
        mockUser.setPhoto(null);
        mockUser.setPermission(new Permission());
        mockUser.setJoiningDate(LocalDateTime.now());
        return mockUser;
    }

    public static User createMockDifferentUser() {
        User mockDifferentUser = createMockUser();
        mockDifferentUser.setId(3L);
        mockDifferentUser.setUsername("MockDifferentUsername");
        mockDifferentUser.setEmail("mockdifferent@mail.com");
        return mockDifferentUser;
    }

    public static UserDTO createMockUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("MockFirstNameDTO");
        userDTO.setLastName("MockLastNameDTO");
        userDTO.setEmail("mockdto@mail.com");
        userDTO.setUsername("MockUsernameDTO");
        userDTO.setPassword("MockPasswordDTO");
        userDTO.setPasswordConfirm("MockPasswordDTO");
        userDTO.setPhoneNumber(null);
        userDTO.setPhoto(null);
        return userDTO;
    }

    public static Post createMockPost() {
        Post mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("mockPostTitle");
        mockPost.setContent("mockPostContent");
        mockPost.setDatecreated(LocalDateTime.now());
        mockPost.setUserCreated(createMockUser());
        return mockPost;
    }
    public static PostOutputDTO createMockPostOutputDto() {
        return new PostOutputDTO(
                "mockPostTitle",
                "mockPostContent",
                "mockUsername",
                0,
                List.of("mocktag"),
                formatToString(LocalDateTime.now()));
    }

    public static PostDTO createPostDto() {
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("**mockPostTitle**");
        postDTO.setContent("*********mockPostContent********");
        postDTO.setTags(List.of("mocktag"));
        return postDTO;
    }

    public static Comment createMockComment() {
        Comment mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setCreatedBy(createMockUser());
        mockComment.setContent("mockCommentContent");
        mockComment.setPostedOn(createMockPost());
        mockComment.setDateCreated(LocalDateTime.now());
        return mockComment;
    }
    public static CommentOutputDTO createMockCommentOutputDTO() {
        CommentOutputDTO commentOutputDTO = new CommentOutputDTO();
        commentOutputDTO.setContent("mockCommentContent");
        commentOutputDTO.setCreatedBy("mockUsername");
        commentOutputDTO.setDateCreated(formatToString(LocalDateTime.now()));
        commentOutputDTO.setPostedOn("mockPostTitle");
        return commentOutputDTO;
    }

    public static String toJson(final Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new Jdk8Module());
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Tag createMockTag() {
        return new Tag("mocktagname");
    }
}
