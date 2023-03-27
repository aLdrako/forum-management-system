package com.telerikacademy.web.fms.helpers;

import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.models.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FilterAndSortParameters {

    /**
     * Method to extract parameters from URL query {path}?sort=***&order=***
     * Used for sorting/ordering comments/posts
     * @param parameters sort and order params
     * @return Map containing sort/order key-values
     */
    public static Map<String, String> extractSortOrder(Map<String, String> parameters) {
        AtomicReference<String> sort = new AtomicReference<>("id");
        AtomicReference<String> order = new AtomicReference<>("asc");
        parameters.forEach((key, value) -> {
            if (key.contains("sort")) sort.set(value);
            if (key.contains("order")) order.set(value);
        });
        return Map.of("sort", sort.get(), "order", order.get());
    }
    public static Map<String, String> extractSortOrderPosts(Map<String, String> parameters) {
        AtomicReference<String> sort = new AtomicReference<>("id");
        AtomicReference<String> order = new AtomicReference<>("asc");
        parameters.forEach((key, value) -> {
            if (key.contains("sort") && value != null) {
                if (value.equalsIgnoreCase("title") || value.equalsIgnoreCase("likes")
                        || value.equalsIgnoreCase("datecreated")
                        || value.equalsIgnoreCase("userid")) {
                    sort.set(value.toLowerCase());
                }
            }
            if (key.contains("order") && value != null) order.set(value);

        });
        return Map.of("sort", sort.get(), "order", order.get());
    }
    public static Predicate extractFilterPredicate(Map<String, String> parameters, CriteriaBuilder builder,
                                                   Root<Post> root, Join<Post, User> userJoin,
                                                   Join<Post, Tag> tagJoin) {
        List<Predicate> predicates = new ArrayList<>();
        if (parameters.containsKey("userId") && !parameters.get("userId").isEmpty()) {
            predicates.add(builder.equal(userJoin.get("id"), parameters.get("userId")));
        }
        if (parameters.containsKey("title")) predicates.add(builder.like(root.get("title"), "%" +
                parameters.get("title") + "%"));
        if (parameters.containsKey("content")) predicates.add(builder.like(root.get("content"), "%" +
                parameters.get("content") + "%"));
        if (parameters.containsKey("tag") && !parameters.get("tag").isEmpty()) {
            predicates.add(builder.equal(tagJoin.get("name"), parameters.get("tag")));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
