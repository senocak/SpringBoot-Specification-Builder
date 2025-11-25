package com.github.senocak.easyspec.util;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

/**
 * Utility class for resolving deep field paths in JPA Criteria API.
 * Handles automatic join creation for nested paths like "address.city.name".
 *
 * @author Spring Boot Easy Specification
 */
public class PathUtils {

    private PathUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Resolves a field path (potentially nested) from the root entity.
     * Automatically creates joins for nested paths.
     *
     * Examples:
     * - "email" -> root.get("email")
     * - "address.city" -> root.join("address").get("city")
     * - "address.city.name" -> root.join("address").join("city").get("name")
     *
     * @param root the root entity
     * @param fieldPath the field path (e.g., "email" or "address.city.name")
     * @param <T> the root entity type
     * @return the resolved Path
     * @throws IllegalArgumentException if the path is invalid
     */
    public static <T> Path<?> resolvePath(Root<T> root, String fieldPath) {
        return resolvePath(root, fieldPath, JoinType.LEFT);
    }

    /**
     * Resolves a field path with explicit join type.
     *
     * @param root the root entity
     * @param fieldPath the field path
     * @param joinType the join type to use for nested paths
     * @param <T> the root entity type
     * @return the resolved Path
     */
    public static <T> Path<?> resolvePath(Root<T> root, String fieldPath, JoinType joinType) {
        if (fieldPath == null || fieldPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Field path cannot be null or empty");
        }

        String[] parts = fieldPath.split("\\.");

        if (parts.length == 1) {
            return root.get(parts[0]);
        }

        // Start from root and navigate through the path
        // Use From<?, ?> to track the current join point (Root and Join both extend From)
        From<?, ?> currentFrom = root;

        // Process all parts except the last one (these require joins)
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];

            // Create a join from the current From (Root or Join)
            // The JPA Criteria API will handle duplicate joins automatically
            @SuppressWarnings("unchecked")
            Join<Object, Object> join = currentFrom.join(part, joinType);
            currentFrom = join;
        }

        // Get the final field from the last path segment
        return currentFrom.get(parts[parts.length - 1]);
    }
}

