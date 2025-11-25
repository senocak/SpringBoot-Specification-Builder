package com.github.senocak.easyspec.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConditionTest {

    @Test
    void testConstructor() {
        Condition condition = new Condition("email", Operator.EQUAL, "test@example.com");
        assertEquals("email", condition.getField());
        assertEquals(Operator.EQUAL, condition.getOperator());
        assertEquals(1, condition.getValues().length);
        assertEquals("test@example.com", condition.getValues()[0]);
    }

    @Test
    void testConstructorWithMultipleValues() {
        Condition condition = new Condition("age", Operator.BETWEEN, 18, 65);
        assertEquals("age", condition.getField());
        assertEquals(Operator.BETWEEN, condition.getOperator());
        assertEquals(2, condition.getValues().length);
        assertEquals(18, condition.getValues()[0]);
        assertEquals(65, condition.getValues()[1]);
    }

    @Test
    void testConstructorWithNullField() {
        assertThrows(NullPointerException.class, () -> {
            new Condition(null, Operator.EQUAL, "value");
        });
    }

    @Test
    void testConstructorWithNullOperator() {
        assertThrows(NullPointerException.class, () -> {
            new Condition("field", null, "value");
        });
    }

    @Test
    void testConstructorWithNullValues() {
        Condition condition = new Condition("field", Operator.IS_NULL, (Object[]) null);
        assertNotNull(condition.getValues());
        assertEquals(0, condition.getValues().length);
    }

    @Test
    void testEquals() {
        Condition condition1 = new Condition("email", Operator.EQUAL, "test@example.com");
        Condition condition2 = new Condition("email", Operator.EQUAL, "test@example.com");
        Condition condition3 = new Condition("name", Operator.EQUAL, "test@example.com");
        Condition condition4 = new Condition("email", Operator.NOT_EQUAL, "test@example.com");

        assertEquals(condition1, condition2);
        assertNotEquals(condition1, condition3);
        assertNotEquals(condition1, condition4);
    }

    @Test
    void testHashCode() {
        Condition condition1 = new Condition("email", Operator.EQUAL, "test@example.com");
        Condition condition2 = new Condition("email", Operator.EQUAL, "test@example.com");

        assertEquals(condition1.hashCode(), condition2.hashCode());
    }

    @Test
    void testToString() {
        Condition condition = new Condition("email", Operator.EQUAL, "test@example.com");
        String toString = condition.toString();
        assertTrue(toString.contains("email"));
        assertTrue(toString.contains("EQUAL"));
        assertTrue(toString.contains("test@example.com"));
    }
}

