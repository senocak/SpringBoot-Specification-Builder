package com.github.senocak.easyspec;

import com.github.senocak.easyspec.builder.SpecBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Usage examples for Spring Boot Easy Specification.
 *
 * This file demonstrates various ways to use the SpecBuilder API.
 * Note: This is a demonstration file - actual entity classes would be defined elsewhere.
 */
public class UsageExamples {

    // Example entity classes (for demonstration only)
    static class User {
        private String email;
        private String name;
        private Integer age;
        private String status;
        private Address address;
    }

    static class Address {
        private String street;
        private City city;
    }

    static class City {
        private String name;
    }

    /**
     * Example 1: Basic equality and string matching
     */
    public static Specification<User> findUserByEmailAndName(String email, String name) {
        return SpecBuilder.forClass(User.class)
                .eq("email", email)
                .contains("name", name)
                .build();
    }

    /**
     * Example 2: Range queries with null-safe filtering
     */
    public static Specification<User> findUsersByAgeRange(Integer minAge, Integer maxAge) {
        return SpecBuilder.forClass(User.class)
                .greaterThan("age", minAge)  // Skipped if minAge is null
                .lessThan("age", maxAge)     // Skipped if maxAge is null
                .build();
    }

    /**
     * Example 3: Deep path resolution with nested entities
     */
    public static Specification<User> findUsersByCity(String cityName) {
        return SpecBuilder.forClass(User.class)
                .eq("address.city.name", cityName)  // Automatically creates joins
                .build();
    }

    /**
     * Example 4: Multiple conditions combined with AND
     */
    public static Specification<User> findActiveUsersInCity(String cityName, Integer minAge) {
        return SpecBuilder.forClass(User.class)
                .eq("status", "ACTIVE")
                .eq("address.city.name", cityName)
                .greaterThan("age", minAge)
                .isNotNull("email")
                .build();
    }

    /**
     * Example 5: IN clause with collection
     */
    public static Specification<User> findUsersByStatuses(List<String> statuses) {
        return SpecBuilder.forClass(User.class)
                .in("status", statuses)  // Automatically skipped if statuses is null or empty
                .build();
    }

    /**
     * Example 6: String pattern matching
     * Note: Current implementation uses AND logic. For OR logic, you would need
     * to combine multiple specifications manually or use Spring's Specification.or()
     */
    public static Specification<User> searchUsers(String searchTerm) {
        // This example shows how you might combine specifications for OR logic
        Specification<User> nameSpec = SpecBuilder.forClass(User.class)
                .contains("name", searchTerm)
                .build();

        Specification<User> emailSpec = SpecBuilder.forClass(User.class)
                .contains("email", searchTerm)
                .build();

        return Specification.where(nameSpec).or(emailSpec);
    }

    /**
     * Example 7: Between clause for date/number ranges
     */
    public static Specification<User> findUsersBetweenAges(Integer startAge, Integer endAge) {
        return SpecBuilder.forClass(User.class)
                .between("age", startAge, endAge)
                .build();
    }

    /**
     * Example 8: Null checks
     */
    public static Specification<User> findUsersWithOrWithoutEmail(Boolean hasEmail) {
        SpecBuilder<User> builder = SpecBuilder.forClass(User.class);
        if (Boolean.TRUE.equals(hasEmail)) {
            builder.isNotNull("email");
        } else {
            builder.isNull("email");
        }
        return builder.build();
    }

    /**
     * Example 9: Complex query with all operators
     */
    public static Specification<User> complexUserSearch(
            String email,
            String name,
            Integer minAge,
            Integer maxAge,
            List<String> statuses,
            String cityName) {
        return SpecBuilder.forClass(User.class)
                .eq("email", email)
                .startsWith("name", name)
                .greaterThan("age", minAge)
                .lessThan("age", maxAge)
                .in("status", statuses)
                .eq("address.city.name", cityName)
                .isNotNull("email")
                .build();
    }

    /**
     * Example 10: Chaining multiple string operations
     */
    public static Specification<User> findUsersByEmailPattern(String domain) {
        return SpecBuilder.forClass(User.class)
                .endsWith("email", domain)  // email LIKE %domain
                .build();
    }

}

