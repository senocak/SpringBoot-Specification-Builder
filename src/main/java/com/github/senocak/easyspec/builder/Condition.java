package com.github.senocak.easyspec.builder;

import java.util.Objects;

/**
 * Represents a single condition in a Specification query.
 * Contains the field path, operator, and values to be used in the condition.
 *
 * @author Spring Boot Easy Specification
 */
public class Condition {
    private final String field;
    private final Operator operator;
    private final Object[] values;

    /**
     * Creates a new Condition.
     *
     * @param field the field path (e.g., "email" or "address.city.name")
     * @param operator the operator to apply
     * @param values the values for the condition (varargs)
     */
    public Condition(String field, Operator operator, Object... values) {
        this.field = Objects.requireNonNull(field, "Field cannot be null");
        this.operator = Objects.requireNonNull(operator, "Operator cannot be null");
        this.values = values != null ? values : new Object[0];
    }

    /**
     * Gets the field path.
     *
     * @return the field path
     */
    public String getField() {
        return field;
    }

    /**
     * Gets the operator.
     *
     * @return the operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * Gets the values array.
     *
     * @return the values array
     */
    public Object[] getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Condition condition = (Condition) o;
        return Objects.equals(field, condition.field) &&
               operator == condition.operator &&
               java.util.Arrays.equals(values, condition.values);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(field, operator);
        result = 31 * result + java.util.Arrays.hashCode(values);
        return result;
    }

    @Override
    public String toString() {
        return "Condition{" +
               "field='" + field + '\'' +
               ", operator=" + operator +
               ", values=" + java.util.Arrays.toString(values) +
               '}';
    }
}

