package com.telerikacademy.web.fms.helpers;

import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.models.dto.CommentOutputDTO;
import com.telerikacademy.web.fms.models.dto.PostOutputDTO;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class FilterAndSortParameters {

    /**
     * Method to extract parameters from URL query {path}?sort=***&order=***&filter=***
     * Used for sorting/ordering/filtering comments/posts
     * @param parameters sort and order params
     * @return Map containing sort/order key-values
     */
    public static Map<String, String> extractParams(Map<String, String> parameters) {
        AtomicReference<String> sort = new AtomicReference<>("dateCreated");
        AtomicReference<String> order = new AtomicReference<>("asc");
        parameters.forEach((key, value) -> {
            if (key.contains("sort")) sort.set(value);
            if (key.contains("order")) order.set(value);
        });
        return Map.of("sort", sort.get(), "order", order.get());
    }

    /**
     * Filter predicate applies to comments/posts collection
     * @param parameters takes only filter value from map parameters
     * @return filter by title/content/tag Predicate
     */
    public static Predicate<Post> getPostFilter(Map<String, String> parameters) {
        if (parameters.get("filter") == null || parameters.get("filter").isEmpty()) return post -> true;
        Predicate<Post> filterTitle = post -> post.getTitle().toLowerCase().contains(parameters.get("filter").toLowerCase());
        Predicate<Post> filterContent = post -> post.getContent().toLowerCase().contains(parameters.get("filter").toLowerCase());
        Predicate<Post> filterTags = post -> post.getTags().stream().map(Tag::getName).anyMatch(name -> name.contains(parameters.get("filter").toLowerCase()));
        return filterTitle.or(filterContent).or(filterTags);
    }

    public static Comparator<CommentOutputDTO> getCommentDTOComparator(Map<String, String> parameters) {
        Map<String, String> params = extractParams(parameters);
        Comparator<CommentOutputDTO> comparator  = switch (params.get("sort")) {
            case "content" -> Comparator.comparing(CommentOutputDTO::getContent);
            case "postedOn" -> Comparator.comparing(CommentOutputDTO::getPostedOn);
            default -> Comparator.comparing(CommentOutputDTO::getDateCreated);
        };
        return (params.get("order").equals("desc")) ? comparator.reversed() : comparator;
    }

    public static Comparator<PostOutputDTO> getPostDTOComparator(Map<String, String> parameters) {
        Map<String, String> params = extractParams(parameters);
        Comparator<PostOutputDTO> comparator = switch (params.get("sort")) {
            case "title" -> Comparator.comparing(PostOutputDTO::getTitle);
            case "content" -> Comparator.comparing(PostOutputDTO::getContent);
            case "postedOn" -> Comparator.comparing(PostOutputDTO::getLikes);
            default -> Comparator.comparing(PostOutputDTO::getDateCreated);
        };
        return (params.get("order").equals("desc")) ? comparator.reversed() : comparator;
    }
}
