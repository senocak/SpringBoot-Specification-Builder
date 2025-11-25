package com.github.senocak.easyspec.util;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class PathUtilsTest {

    @Mock
    private Root<TestEntity> root;

    @Mock
    private Path<String> simplePath;

    @Mock
    private Join<Object, Object> firstJoin;

    @Mock
    private Join<Object, Object> secondJoin;

    @Mock
    private Path<String> nestedPath;

    private static class TestEntity {
        private String email;
        private Address address;
    }

    private static class Address {
        private City city;
    }

    private static class City {
        private String name;
    }

    @BeforeEach
    void setUp() {
        when(root.get("email")).thenReturn((Path) simplePath);
        when(root.join(eq("address"), eq(JoinType.LEFT))).thenReturn(firstJoin);
        when(firstJoin.join(eq("city"), eq(JoinType.LEFT))).thenReturn(secondJoin);
        when(secondJoin.get("name")).thenReturn((Path) nestedPath);
    }

    @Test
    void testResolveSimplePath() {
        Path<?> path = PathUtils.resolvePath(root, "email");
        assertNotNull(path);
        verify(root).get("email");
        verify(root, never()).join(anyString(), any(JoinType.class));
    }

    @Test
    void testResolveNestedPath() {
        Path<?> path = PathUtils.resolvePath(root, "address.city.name");
        assertNotNull(path);
        verify(root).join("address", JoinType.LEFT);
        verify(firstJoin).join("city", JoinType.LEFT);
        verify(secondJoin).get("name");
    }

    @Test
    void testResolveTwoLevelPath() {
        when(firstJoin.get("street")).thenReturn((Path) simplePath);
        Path<?> path = PathUtils.resolvePath(root, "address.street");
        assertNotNull(path);
        verify(root).join(eq("address"), eq(JoinType.LEFT));
        verify(firstJoin).get("street");
    }

    @Test
    void testResolvePathWithNullField() {
        assertThrows(IllegalArgumentException.class, () -> {
            PathUtils.resolvePath(root, null);
        });
    }

    @Test
    void testResolvePathWithEmptyField() {
        assertThrows(IllegalArgumentException.class, () -> {
            PathUtils.resolvePath(root, "");
        });
    }

    @Test
    void testResolvePathWithWhitespaceField() {
        assertThrows(IllegalArgumentException.class, () -> {
            PathUtils.resolvePath(root, "   ");
        });
    }

    @Test
    void testResolvePathWithExplicitJoinType() {
        Join<Object, Object> innerJoin = mock(Join.class);
        when(root.join(eq("address"), eq(JoinType.INNER))).thenReturn(innerJoin);
        when(innerJoin.get("city")).thenReturn((Path) simplePath);

        Path<?> path = PathUtils.resolvePath(root, "address.city", JoinType.INNER);
        assertNotNull(path);
        verify(root).join(eq("address"), eq(JoinType.INNER));
    }

    @Test
    void testResolvePathDefaultJoinTypeIsLeft() {
        when(firstJoin.get("city")).thenReturn((Path) simplePath);
        Path<?> path = PathUtils.resolvePath(root, "address.city");
        assertNotNull(path);
        verify(root).join(eq("address"), eq(JoinType.LEFT));
    }
}

