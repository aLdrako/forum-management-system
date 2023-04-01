package com.telerikacademy.web.fms.controllers.rest;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.PostOutputDTO;
import com.telerikacademy.web.fms.services.contracts.TagServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag Rest Controller", description = "Tag management API")
@RestController
@RequestMapping("/api/tags")
public class TagRestController {

    private final TagServices tagServices;
    private final AuthenticationHelper authenticationHelper;
    @Autowired
    public TagRestController(TagServices tagServices, AuthenticationHelper authenticationHelper) {
        this.tagServices = tagServices;
        this.authenticationHelper = authenticationHelper;
    }
    @Operation(summary = "Get All Tags", description = "Get a list of all Tags",
            tags = {"tags", "get all"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of Tag objects",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tag.class, type = "array"))}),
            @ApiResponse(responseCode = "401", description = "The user that is trying to get all tags " +
                    "is not found or not an admin.",
                    content = @Content),
    })
    @GetMapping
    public List<Tag> getAll(@RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return tagServices.getAll(user);
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @Operation(summary = "Get a tag", description = "Get a tag from database",
            tags = {"tag", "get tag"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag object",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tag.class))}),
            @ApiResponse(responseCode = "401", description = "The user that is trying to get a tag " +
                    "is not found or not an admin.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag does not exist", content =
                    {@Content(schema = @Schema())}),
    })
    @GetMapping("/{id}")
    public Tag getById(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return tagServices.getTagById(id, user);
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Delete a tag", description = "Delete a tag from database",
            tags = {"tag", "delete tag"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag object deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tag.class))}),
            @ApiResponse(responseCode = "401", description = "The user that is trying to remove the tag " +
                    "is not found or not an admin.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag does not exist", content =
                    {@Content(schema = @Schema())}),
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            tagServices.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
