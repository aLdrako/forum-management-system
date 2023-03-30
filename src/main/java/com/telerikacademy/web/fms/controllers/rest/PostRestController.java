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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@Tag(name = "Post Rest Controller", description = "Post management API")
@RestController
@RequestMapping("/api/posts")
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
    @Operation(summary = "Get All Posts", description = "Get a list of all PostOutputDto objects based on" +
            " the parameters", tags = {"posts", "get all", "sort", "filter"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of PostOutputDto objects",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PostOutputDTO.class, type = "array"))})
    })
    @Parameters({
            @Parameter(name = "sort", example = "likes", description = "Sort by title, likes, dateCreated, userId", schema = @Schema(type = "string")),
            @Parameter(name = "order", example = "desc", description = "Order by Ascending (asc) or Descending (desc)", schema = @Schema(type = "string")),
            @Parameter(name = "title", example = "Title", description = "Filter by title(specified sequence of char values)", schema = @Schema(type = "string")),
            @Parameter(name = "content", example = "Content", description = "Filter by content (specified sequence of char values)", schema = @Schema(type = "string")),
            @Parameter(name = "tag", example = "knowledge", description = "Filter by tag (exact value)", schema = @Schema(type = "string")),
            @Parameter(name = "tag", example = "knowledge", description = "Filter by tag (exact value)", schema = @Schema(type = "string")),
            @Parameter(name = "username", example = "username", description = "Filter by username (exact value)", schema = @Schema(type = "string")),

    })
    @GetMapping
    public List<PostOutputDTO> getAll(@Parameter(hidden = true) @RequestParam Map<String, String> parameters) {
        parameters.remove("userId");
        return postMapper.objectToDto(postServices.getAll(parameters));
    }

    @Operation(summary = "Get a specific post", description = "Get a specific PostOutputDTO object if it exists",
    tags = {"get by id", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns an object that Exists",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostOutputDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Object does not exist", content =
                    {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public PostOutputDTO getById(@PathVariable Long id) {
        try {
           Post post = postServices.getById(id);
           return postMapper.objectToDto(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @Operation(summary = "Create a new post", description = "Create a new post with the fields written in the body",
    tags = {"create post", "new"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns the new Post",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostOutputDTO.class))}),
            @ApiResponse(responseCode = "400", description = "The validation of the body has failed",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "The user that is trying to create a post " +
                    "is not found/is blocked.",
                    content = @Content)
    })
    @PostMapping
    public PostOutputDTO create(@Valid @RequestBody PostDTO postDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.dtoToObject(postDTO);
            post.setUserCreated(user);
            Post postCreated = postServices.create(post, user);
            return postMapper.objectToDto(postCreated);
        }  catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Operation(summary = "Update an existing post", description = "A post id is passed with a body of the new Post and " +
            "if the validation passes the updated post is returned", tags = {"post", "update post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns the updated Post",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostOutputDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Object does not exist", content =
                    {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", description = "The user that is trying to update a post " +
                    "is not found/is blocked.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "The validation of the body has failed",
                    content = @Content)
    })
    @PutMapping("/{id}")
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
    
    @Operation(summary = "Adds/Removes a like from post", description = "If the user has liked the post, the like will be removed" +
            "and vice versa", tags = {"like post", "remove like from post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Likes of post are updated"),
            @ApiResponse(responseCode = "404", description = "Post Not Found"),
            @ApiResponse(responseCode = "404", description = "Unauthorized access")
    })
    @PutMapping("/{id}/like")
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
    @Operation(summary = "Delete an existing post", description = "Delete an existing post",
            tags = {"delete post", "delete"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post deleted"),
            @ApiResponse(responseCode = "404", description = "Post Not Found"),
            @ApiResponse(responseCode = "404", description = "Unauthorized access")
    })
    @DeleteMapping("/{id}")
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

    @Operation(summary = "Get all comments of a post", description = "Get all comments of a post if the post exists",
            tags = {"comments of post", "comments"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of CommentOutputDTO objects",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentOutputDTO.class, type="array"))}),
            @ApiResponse(responseCode = "404", description = "Post Not Found", content = {@Content(schema = @Schema())}),
    })
    @Parameters({
            @Parameter(name = "sort", example = "content", description = "Sort by content, postedOn, dateCreated", schema = @Schema(type = "string")),
            @Parameter(name = "order", example = "desc", description = "Order by Ascending (asc) or Descending (desc)", schema = @Schema(type = "string"))
    })
    @GetMapping("/{postId}/comments")
    public List<CommentOutputDTO> getCommentsByPostId(@PathVariable Long postId, @RequestParam Map<String, String> parameters) {
        try {
            return commentServices.getCommentsByPostId(postId, parameters).stream().map(postMapper::objectToDto).toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @Operation(
            summary = "Get Comment of a certain post by ID",
            description = "Get a CommentOutputDTO object by its ID and post ID",
            tags = {"comment of post", "get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment found",
                    content = { @Content(schema = @Schema(implementation = CommentOutputDTO.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Comment/Post not found", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{postId}/comments/{commentId}")
    public CommentOutputDTO getCommentByPostId(@PathVariable Long postId, @PathVariable Long commentId) {
        try {
            Comment comment = commentServices.getCommentByPostId(postId, commentId);
            return postMapper.objectToDto(comment);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @Operation(summary = "Search All Posts", description = "Get a list of all PostOutputDto objects based on" +
            " a key word", tags = {"posts", "search all"})
    @ApiResponse(responseCode = "200", description = "List of PostOutputDto objects",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PostOutputDTO.class, type = "array"))})
    @Parameters({
            @Parameter(name = "q", example = "knowledge", description = "Keyword for searching", schema = @Schema(type = "string")),
    })
    @GetMapping("/search")
    public List<PostOutputDTO> searchPosts(@RequestParam (required = false) Optional<String> q) {
        return postMapper.objectToDto(postServices.search(q));
    }
}
