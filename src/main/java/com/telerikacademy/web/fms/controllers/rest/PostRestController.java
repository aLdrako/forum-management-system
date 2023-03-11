package com.telerikacademy.web.fms.controllers.rest;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.dto.CommentOutputDTO;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.dto.PostDTO;
import com.telerikacademy.web.fms.models.dto.PostOutputDTO;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.telerikacademy.web.fms.helpers.FilterAndSortParameters.getCommentDTOComparator;

@RestController
@RequestMapping("/api")
public class PostRestController {
    private static final String POST_NO_COMMENT_WITH_ID = "Post with id %d does not have comment with id %d!";
    private final PostServices postServices;
    private final ModelMapper postMapper;
    private final AuthenticationHelper authenticationHelper;

    public PostRestController(PostServices postServices, ModelMapper postMapper,
                              AuthenticationHelper authenticationHelper) {
        this.postServices = postServices;
        this.postMapper = postMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/posts")
    public List<PostOutputDTO> getAll(@RequestParam(required = false) Optional<String> title,
                                      @RequestParam(required = false) Optional<String> sortBy,
                                      @RequestParam(required = false) Optional<String> orderBy) {
        return postMapper.objectToDto(postServices.getAll(Optional.empty(), title, sortBy, orderBy));
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
    public List<CommentOutputDTO> getPostComments(@PathVariable Long postId, @RequestParam Map<String, String> parameters) {
        try {
            return postServices.getById(postId).getComments().stream()
                    .map(postMapper::objectToDto)
                    .sorted(getCommentDTOComparator(parameters)).toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("posts/{postId}/comments/{commentId}")
    public CommentOutputDTO getPostComment(@PathVariable Long postId, @PathVariable Long commentId) {
        try {
            return postServices.getById(postId).getComments().stream()
                    .filter(comment -> comment.getId().equals(commentId))
                    .map(postMapper::objectToDto).findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(String.format(POST_NO_COMMENT_WITH_ID, postId, commentId)));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/posts/search")
    public List<PostOutputDTO> searchPosts(@RequestParam Map<String, String> param) {
        return postMapper.objectToDto(postServices.search(param));
    }
}
