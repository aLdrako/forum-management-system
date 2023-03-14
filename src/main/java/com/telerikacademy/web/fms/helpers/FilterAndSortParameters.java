package com.telerikacademy.web.fms.helpers;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FilterAndSortParameters {

    /**
     * Method to extract parameters from URL query {path}?sort=***&order=***
     * Used for sorting/ordering comments/posts
     * @param parameters sort and order params
     * @return Map containing sort/order key-values
     */
    public static Map<String, String> extractParams(Map<String, String> parameters) {
        AtomicReference<String> sort = new AtomicReference<>("id");
        AtomicReference<String> order = new AtomicReference<>("asc");
        parameters.forEach((key, value) -> {
            if (key.contains("sort")) sort.set(value);
            if (key.contains("order")) order.set(value);
        });
        return Map.of("sort", sort.get(), "order", order.get());
    }
}
