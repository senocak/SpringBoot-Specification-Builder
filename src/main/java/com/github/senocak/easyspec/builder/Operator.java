package com.github.senocak.easyspec.builder;

/**
 * Enum representing different comparison operators for building JPA Specifications.
 *
 * @author Spring Boot Easy Specification
 */
public enum Operator {
    /**
     * Equality operator (=)
     */
    EQUAL,

    /**
     * Not equal operator (!=)
     */
    NOT_EQUAL,

    /**
     * Greater than operator (>)
     */
    GREATER_THAN,

    /**
     * Less than operator (<)
     */
    LESS_THAN,

    /**
     * Greater than or equal operator (>=)
     */
    GREATER_THAN_OR_EQUAL,

    /**
     * Less than or equal operator (<=)
     */
    LESS_THAN_OR_EQUAL,

    /**
     * LIKE operator with wildcards on both sides (%value%)
     */
    CONTAINS,

    /**
     * LIKE operator with wildcard at the end (value%)
     */
    STARTS_WITH,

    /**
     * LIKE operator with wildcard at the beginning (%value)
     */
    ENDS_WITH,

    /**
     * IN operator for checking membership in a collection
     */
    IN,

    /**
     * BETWEEN operator for range checks
     */
    BETWEEN,

    /**
     * IS NULL operator
     */
    IS_NULL,

    /**
     * IS NOT NULL operator
     */
    IS_NOT_NULL,

    /**
     * ORDER BY operator for sorting results
     */
    ORDER_BY
}

