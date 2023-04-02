package com.telerikacademy.web.fms.controllers.rest;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.CommentDTO;
import com.telerikacademy.web.fms.models.dto.CommentOutputDTO;
import com.telerikacademy.web.fms.models.validations.CreateValidationGroup;
import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.services.contracts.CommentServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Tag(name = "Comment Rest Controller", description = "Comment management API")
@RestController
@RequestMapping("/api/comments")
public class CommentRestController {
    private final ModelMapper modelMapper;
    private final CommentServices commentServices;
    private final AuthenticationHelper authenticationHelper;

    public CommentRestController(ModelMapper modelMapper, CommentServices commentServices, AuthenticationHelper authenticationHelper) {
        this.modelMapper = modelMapper;
        this.commentServices = commentServices;
        this.authenticationHelper = authenticationHelper;
    }

    @Operation(
            summary = "Get all Comments",
            description = "Get a list of all CommentOutputDTO objects based on specified query parameters",
            tags = {"comments", "get all", "sort"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of CommentOutputDTO objects",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentOutputDTO.class, type = "array"))})
    })
    @Parameters({
            @Parameter(name = "sort", example = "content", description = "Sort by content, postedOn, dateCreated", schema = @Schema(type = "string")),
            @Parameter(name = "order", example = "desc", description = "Order by Ascending (asc) or Descending (desc)", schema = @Schema(type = "string"))
    })
    @GetMapping
    public List<CommentOutputDTO> getAll(@Parameter(hidden = true) @RequestParam Map<String, String> parameter) {
        return commentServices.getAll(parameter).stream().map(modelMapper::objectToDto).toList();
    }

    @Operation(
            summary = "Get Comment by ID",
            description = "Get a Comment object by its ID",
            tags = {"comments", "get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment found",
                    content = {@Content(schema = @Schema(implementation = CommentOutputDTO.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public CommentOutputDTO getById(@PathVariable Long id) {
        try {
            Comment comment = commentServices.getById(id);
            return modelMapper.objectToDto(comment);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Create a new Comment",
            description = "Create a new Comment object",
            tags = {"comments", "create"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment created",
                    content = {@Content(schema = @Schema(implementation = CommentOutputDTO.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401", description = "Unauthorized operation", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post Not Found", content = @Content),
            @ApiResponse(responseCode = "400", description = "The validation of the body has failed", content = {@Content(schema = @Schema())})
    })
    @PostMapping
    public CommentOutputDTO create(@Validated(CreateValidationGroup.class) @RequestBody CommentDTO commentDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Comment comment = modelMapper.dtoToObject(commentDTO);
            comment.setCreatedBy(user);
            return modelMapper.objectToDto(commentServices.create(comment, user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Operation(
            summary = "Update Comment by ID",
            description = "Update an existing Comment by ID",
            tags = {"comments", "update"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommentOutputDTO.class))}
            ),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
            @ApiResponse(responseCode = "400", description = "The validation of the body has failed", content = {@Content(schema = @Schema())})
    })
    @PutMapping("/{id}")
    public CommentOutputDTO update(@PathVariable Long id, @Validated(UpdateValidationGroup.class) @RequestBody CommentDTO commentDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Comment comment = modelMapper.dtoToObject(id, commentDTO);
            return modelMapper.objectToDto(commentServices.update(comment, user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Operation(
            summary = "Delete a Comment by ID",
            description = "Delete a Comment object based on the given ID",
            tags = {"comments", "delete"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            commentServices.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
