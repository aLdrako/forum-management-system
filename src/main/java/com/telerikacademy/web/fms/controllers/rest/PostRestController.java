package com.telerikacademy.web.fms.controllers.rest;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.dto.CommentOutputDTO;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.dto.PostDTO;
import com.telerikacademy.web.fms.models.dto.PostOutputDTO;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.services.contracts.CommentServices;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostRestController {
    private final PostServices postServices;
    private final CommentServices commentServices;
    private final ModelMapper postMapper;
    private final AuthenticationHelper authenticationHelper;

    public PostRestController(PostServices postServices, CommentServices commentServices, ModelMapper postMapper,
                              AuthenticationHelper authenticationHelper) {
        this.postServices = postServices;
        this.commentServices = commentServices;
        this.postMapper = postMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/posts")
    public List<PostOutputDTO> getAll(@RequestParam(required = false) Optional<String> title,
                                      @RequestParam(required = false) Optional<String> content,
                                      @RequestParam(required = false) Optional<String> sort,
                                      @RequestParam(required = false) Optional<String> order) {
        return postMapper.objectToDto(postServices.getAll(Optional.empty(), title, content, sort, order));
    }

    @GetMapping("/posts/{id}")
    public PostOutputDTO getById(@PathVariable Long id) {
        try {
           Post post = postServices.getById(id);
           return postMapper.objectToDto(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/posts")
    public PostOutputDTO create(@Valid @RequestBody PostDTO postDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.dtoToObject(postDTO);
            post.setUserCreated(user);
            Post postCreated = postServices.create(post, user);
            return postMapper.objectToDto(postCreated);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/posts/{id}")
    public PostOutputDTO update(@PathVariable Long id, @Valid @RequestBody PostDTO postDTO,
                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.dtoToObject(id, postDTO);
            postServices.update(post, user);
            postServices.updateTagsInPost(postDTO.getTags(), post);
            return postMapper.objectToDto(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException |  AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/posts/{id}/like")
    public void changePostLikes(@PathVariable Long id,
                                @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            postServices.changePostLikes(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException | AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/posts/{id}")
    public void delete(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            postServices.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException | AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("posts/{postId}/comments")
    public List<CommentOutputDTO> getCommentsByPostId(@PathVariable Long postId, @RequestParam Map<String, String> parameters) {
        try {
            return commentServices.getCommentsByPostId(postId, parameters).stream().map(postMapper::objectToDto).toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("posts/{postId}/comments/{commentId}")
    public CommentOutputDTO getCommentByPostId(@PathVariable Long postId, @PathVariable Long commentId) {
        try {
            Comment comment = commentServices.getCommentByPostId(postId, commentId);
            return postMapper.objectToDto(comment);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/posts/search")
    public List<PostOutputDTO> searchPosts(@RequestParam (required = false) Optional<String> q) {
        return postMapper.objectToDto(postServices.search(q));
    }
}
