package com.github.senocak.easyspec.builder;

import com.github.senocak.easyspec.util.PathUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Fluent builder for creating Spring Data JPA Specifications.
 *
 * <p>This builder provides a convenient way to create complex JPA Specifications
 * without writing verbose Criteria API code.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Specification<User> spec = SpecBuilder.forClass(User.class)
 *     .eq("email", email)
 *     .contains("name", name)
 *     .greaterThan("age", 18)
 *     .build();
 * }</pre>
 *
 * <p>All methods are null-safe - if a value is null, that condition is skipped.</p>
 *
 * @param <T> the entity type
 * @author Spring Boot Easy Specification
 */
public class SpecBuilder<T> {

    private final Class<T> entityClass;
    private final List<Condition> conditions;

    /**
     * Private constructor. Use {@link #forClass(Class)} to create instances.
     *
     * @param entityClass the entity class
     */
    private SpecBuilder(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.conditions = new ArrayList<>();
    }

    /**
     * Creates a new SpecBuilder for the given entity class.
     *
     * @param entityClass the entity class
     * @param <T> the entity type
     * @return a new SpecBuilder instance
     */
    public static <T> SpecBuilder<T> forClass(Class<T> entityClass) {
        return new SpecBuilder<>(entityClass);
    }

    /**
     * Adds an equality condition (field = value).
     *
     * @param field the field path
     * @param value the value to compare
     * @return this builder for method chaining
     */
    public SpecBuilder<T> eq(String field, Object value) {
        if (value != null) {
            conditions.add(new Condition(field, Operator.EQUAL, value));
        }
        return this;
    }

    /**
     * Adds a not-equal condition (field != value).
     *
     * @param field the field path
     * @param value the value to compare
     * @return this builder for method chaining
     */
    public SpecBuilder<T> ne(String field, Object value) {
        if (value != null) {
            conditions.add(new Condition(field, Operator.NOT_EQUAL, value));
        }
        return this;
    }

    /**
     * Adds a greater-than condition (field > value).
     *
     * @param field the field path
     * @param value the value to compare
     * @return this builder for method chaining
     */
    public SpecBuilder<T> greaterThan(String field, Comparable<?> value) {
        if (value != null) {
            conditions.add(new Condition(field, Operator.GREATER_THAN, value));
        }
        return this;
    }

    /**
     * Adds a less-than condition (field < value).
     *
     * @param field the field path
     * @param value the value to compare
     * @return this builder for method chaining
     */
    public SpecBuilder<T> lessThan(String field, Comparable<?> value) {
        if (value != null) {
            conditions.add(new Condition(field, Operator.LESS_THAN, value));
        }
        return this;
    }

    /**
     * Adds a greater-than-or-equal condition (field >= value).
     *
     * @param field the field path
     * @param value the value to compare
     * @return this builder for method chaining
     */
    public SpecBuilder<T> gte(String field, Comparable<?> value) {
        if (value != null) {
            conditions.add(new Condition(field, Operator.GREATER_THAN_OR_EQUAL, value));
        }
        return this;
    }

    /**
     * Adds a less-than-or-equal condition (field <= value).
     *
     * @param field the field path
     * @param value the value to compare
     * @return this builder for method chaining
     */
    public SpecBuilder<T> lte(String field, Comparable<?> value) {
        if (value != null) {
            conditions.add(new Condition(field, Operator.LESS_THAN_OR_EQUAL, value));
        }
        return this;
    }

    /**
     * Adds a contains condition (field LIKE %value%).
     *
     * @param field the field path
     * @param value the value to search for
     * @return this builder for method chaining
     */
    public SpecBuilder<T> contains(String field, String value) {
        if (value != null && !value.trim().isEmpty()) {
            conditions.add(new Condition(field, Operator.CONTAINS, value));
        }
        return this;
    }

    /**
     * Adds a starts-with condition (field LIKE value%).
     *
     * @param field the field path
     * @param value the value to search for
     * @return this builder for method chaining
     */
    public SpecBuilder<T> startsWith(String field, String value) {
        if (value != null && !value.trim().isEmpty()) {
            conditions.add(new Condition(field, Operator.STARTS_WITH, value));
        }
        return this;
    }

    /**
     * Adds an ends-with condition (field LIKE %value).
     *
     * @param field the field path
     * @param value the value to search for
     * @return this builder for method chaining
     */
    public SpecBuilder<T> endsWith(String field, String value) {
        if (value != null && !value.trim().isEmpty()) {
            conditions.add(new Condition(field, Operator.ENDS_WITH, value));
        }
        return this;
    }

    /**
     * Adds an IN condition (field IN (values...)).
     *
     * @param field the field path
     * @param values the collection of values
     * @return this builder for method chaining
     */
    public SpecBuilder<T> in(String field, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            conditions.add(new Condition(field, Operator.IN, values.toArray()));
        }
        return this;
    }

    /**
     * Adds a between condition (field BETWEEN start AND end).
     *
     * @param field the field path
     * @param start the start value (inclusive)
     * @param end the end value (inclusive)
     * @return this builder for method chaining
     */
    public SpecBuilder<T> between(String field, Comparable<?> start, Comparable<?> end) {
        if (start != null && end != null) {
            conditions.add(new Condition(field, Operator.BETWEEN, start, end));
        }
        return this;
    }

    /**
     * Adds an IS NULL condition (field IS NULL).
     *
     * @param field the field path
     * @return this builder for method chaining
     */
    public SpecBuilder<T> isNull(String field) {
        conditions.add(new Condition(field, Operator.IS_NULL));
        return this;
    }

    /**
     * Adds an IS NOT NULL condition (field IS NOT NULL).
     *
     * @param field the field path
     * @return this builder for method chaining
     */
    public SpecBuilder<T> isNotNull(String field) {
        conditions.add(new Condition(field, Operator.IS_NOT_NULL));
        return this;
    }

    /**
     * Builds the final Specification by combining all conditions with AND logic.
     *
     * @return the Spring Data JPA Specification
     */
    public Specification<T> build() {
        if (conditions.isEmpty()) {
            return Specification.where(null);
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (Condition condition : conditions) {
                Predicate predicate = buildPredicate(root, query, criteriaBuilder, condition);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Builds a single predicate from a condition.
     *
     * @param root the root entity
     * @param query the criteria query
     * @param cb the criteria builder
     * @param condition the condition to build
     * @return the predicate
     */
    @SuppressWarnings("unchecked")
    private Predicate buildPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb, Condition condition) {
        Path<?> path = PathUtils.resolvePath(root, condition.getField());
        Operator operator = condition.getOperator();
        Object[] values = condition.getValues();

        return switch (operator) {
            case EQUAL -> cb.equal(path, values[0]);
            case NOT_EQUAL -> cb.notEqual(path, values[0]);
            case GREATER_THAN -> cb.greaterThan((Path<Comparable>) path, (Comparable) values[0]);
            case LESS_THAN -> cb.lessThan((Path<Comparable>) path, (Comparable) values[0]);
            case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) values[0]);
            case LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) values[0]);
            case CONTAINS -> cb.like(cb.lower((Path<String>) path), "%" + values[0].toString().toLowerCase() + "%");
            case STARTS_WITH -> cb.like(cb.lower((Path<String>) path), values[0].toString().toLowerCase() + "%");
            case ENDS_WITH -> cb.like(cb.lower((Path<String>) path), "%" + values[0].toString().toLowerCase());
            case IN -> {
                jakarta.persistence.criteria.CriteriaBuilder.In<Object> inClause = cb.in(path);
                for (Object value : values) {
                    inClause.value(value);
                }
                yield inClause;
            }
            case BETWEEN -> cb.between((Path<Comparable>) path, (Comparable) values[0], (Comparable) values[1]);
            case IS_NULL -> cb.isNull(path);
            case IS_NOT_NULL -> cb.isNotNull(path);
        };
    }
}

