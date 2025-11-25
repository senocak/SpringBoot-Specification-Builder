package com.github.senocak.easyspec.builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class SpecBuilderTest {

    @Mock
    private Root<TestEntity> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<String> stringPath;

    @Mock
    private Path<Integer> intPath;

    @Mock
    private Path<Comparable> comparablePath;

    @Mock
    private Predicate predicate;

    @Mock
    private jakarta.persistence.criteria.CriteriaBuilder.In<Object> inClause;

    private static class TestEntity {
        private String email;
        private String name;
        private Integer age;
    }

    @BeforeEach
    void setUp() {
        when(root.get("email")).thenReturn((Path) stringPath);
        when(root.get("name")).thenReturn((Path) stringPath);
        when(root.get("age")).thenReturn((Path) intPath);
        when(cb.equal(any(), any())).thenReturn(predicate);
        when(cb.notEqual(any(), any())).thenReturn(predicate);
        when(cb.greaterThan(any(Path.class), any(Comparable.class))).thenReturn(predicate);
        when(cb.lessThan(any(Path.class), any(Comparable.class))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(any(Path.class), any(Comparable.class))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(any(Path.class), any(Comparable.class))).thenReturn(predicate);
        when(cb.like(any(Path.class), anyString())).thenReturn(predicate);
        when(cb.isNull(any())).thenReturn(predicate);
        when(cb.isNotNull(any())).thenReturn(predicate);
        when(cb.between(any(Path.class), any(Comparable.class), any(Comparable.class))).thenReturn(predicate);
        when(cb.lower(any(Path.class))).thenReturn(stringPath);
        when(cb.in(any(Path.class))).thenReturn(inClause);
        when(inClause.value(any())).thenReturn(inClause);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        when(cb.conjunction()).thenReturn(predicate);
    }

    @Test
    void testForClass() {
        SpecBuilder<TestEntity> builder = SpecBuilder.forClass(TestEntity.class);
        assertNotNull(builder);
    }

    @Test
    void testEq() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .eq("email", "test@example.com")
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).equal(stringPath, "test@example.com");
    }

    @Test
    void testEqWithNullValue() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .eq("email", null)
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb, never()).equal(any(), any());
    }

    @Test
    void testNe() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .ne("email", "test@example.com")
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).notEqual(stringPath, "test@example.com");
    }

    @Test
    void testGreaterThan() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .greaterThan("age", 18)
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).greaterThan(intPath, 18);
    }

    @Test
    void testLessThan() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .lessThan("age", 65)
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).lessThan(intPath, 65);
    }

    @Test
    void testGte() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .gte("age", 18)
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).greaterThanOrEqualTo(intPath, 18);
    }

    @Test
    void testLte() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .lte("age", 65)
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).lessThanOrEqualTo(intPath, 65);
    }

    @Test
    void testContains() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .contains("name", "john")
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).lower(any(Path.class));
        verify(cb).like(any(Path.class), eq("%john%"));
    }

    @Test
    void testContainsWithNullValue() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .contains("name", null)
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb, never()).like(any(Path.class), anyString());
    }

    @Test
    void testContainsWithEmptyValue() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .contains("name", "   ")
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb, never()).like(any(Path.class), anyString());
    }

    @Test
    void testStartsWith() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .startsWith("name", "john")
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).lower(any(Path.class));
        verify(cb).like(any(Path.class), eq("john%"));
    }

    @Test
    void testEndsWith() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .endsWith("name", "doe")
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).lower(any(Path.class));
        verify(cb).like(any(Path.class), eq("%doe"));
    }

    @Test
    void testIn() {
        List<String> emails = Arrays.asList("test1@example.com", "test2@example.com");
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .in("email", emails)
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(root).get("email");
    }

    @Test
    void testInWithNullCollection() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .in("email", null)
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb, never()).in(any());
    }

    @Test
    void testInWithEmptyCollection() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .in("email", Collections.emptyList())
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb, never()).in(any());
    }

    @Test
    void testBetween() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .between("age", 18, 65)
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).between(intPath, 18, 65);
    }

    @Test
    void testBetweenWithNullValues() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .between("age", null, 65)
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb, never()).between(any(Path.class), any(Comparable.class), any(Comparable.class));
    }

    @Test
    void testIsNull() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .isNull("email")
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).isNull(stringPath);
    }

    @Test
    void testIsNotNull() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .isNotNull("email")
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).isNotNull(stringPath);
    }

    @Test
    void testMultipleConditions() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .eq("email", "test@example.com")
                .contains("name", "john")
                .greaterThan("age", 18)
                .build();

        assertNotNull(spec);
        spec.toPredicate(root, query, cb);
        verify(cb).equal(any(), eq("test@example.com"));
        verify(cb).like(any(Path.class), anyString());
        verify(cb).greaterThan(any(Path.class), eq(18));
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    void testEmptyBuilder() {
        Specification<TestEntity> spec = SpecBuilder.forClass(TestEntity.class)
                .build();

        assertNotNull(spec);
        // Empty builder returns Specification.where(null), which returns null predicate
        // So we just verify the spec is not null and can be called
        Predicate result = spec.toPredicate(root, query, cb);
        // Result may be null for empty specification
        assertTrue(result == null || result == predicate);
    }

    @Test
    void testFluentChaining() {
        SpecBuilder<TestEntity> builder = SpecBuilder.forClass(TestEntity.class)
                .eq("email", "test@example.com")
                .ne("name", "admin")
                .greaterThan("age", 18)
                .lessThan("age", 65)
                .gte("age", 18)
                .lte("age", 65);

        assertNotNull(builder);
        Specification<TestEntity> spec = builder.build();
        assertNotNull(spec);
    }
}

