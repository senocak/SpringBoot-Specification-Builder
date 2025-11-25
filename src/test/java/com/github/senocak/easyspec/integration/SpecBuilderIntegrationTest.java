package com.github.senocak.easyspec.integration;

import com.github.senocak.easyspec.builder.SpecBuilder;
import com.github.senocak.easyspec.integration.config.TestConfig;
import com.github.senocak.easyspec.integration.entity.Address;
import com.github.senocak.easyspec.integration.entity.City;
import com.github.senocak.easyspec.integration.entity.User;
import com.github.senocak.easyspec.integration.repository.UserRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.yml")
class SpecBuilderIntegrationTest {
    @Inject private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Create cities
        City newYork = new City("New York", "USA");
        City london = new City("London", "UK");
        City paris = new City("Paris", "France");

        // Create addresses
        Address address1 = new Address("123 Main St", newYork);
        Address address2 = new Address("456 Oak Ave", london);
        Address address3 = new Address("789 Elm St", paris);
        Address address4 = new Address("321 Pine Rd", newYork);

        // Create users
        user1 = new User("john.doe@example.com", "John Doe", 25, User.UserStatus.ACTIVE);
        user1.setAddress(address1);

        user2 = new User("jane.smith@example.com", "Jane Smith", 30, User.UserStatus.ACTIVE);
        user2.setAddress(address2);

        user3 = new User("bob.jones@example.com", "Bob Jones", 35, User.UserStatus.INACTIVE);
        user3.setAddress(address3);

        user4 = new User("alice.brown@example.com", "Alice Brown", 28, User.UserStatus.PENDING);
        user4.setAddress(address4);

        userRepository.saveAll(List.of(user1, user2, user3, user4));
    }

    @Test
    void testEq() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .eq("email", "john.doe@example.com")
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).getName());
    }

    @Test
    void testNe() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .ne("status", User.UserStatus.DELETED)
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(4, users.size());
    }

    @Test
    void testGreaterThan() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .greaterThan("age", 28)
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(u -> u.getAge() > 28));
    }

    @Test
    void testLessThan() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .lessThan("age", 30)
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(u -> u.getAge() < 30));
    }

    @Test
    void testGte() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .gte("age", 30)
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(u -> u.getAge() >= 30));
    }

    @Test
    void testLte() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .lte("age", 30)
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(3, users.size());
        assertTrue(users.stream().allMatch(u -> u.getAge() <= 30));
    }

    @Test
    void testContains() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .contains("name", "John")
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(1, users.size());
        assertTrue(users.get(0).getName().contains("John"));
    }

    @Test
    void testStartsWith() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .startsWith("email", "john")
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(1, users.size());
        assertTrue(users.get(0).getEmail().startsWith("john"));
    }

    @Test
    void testEndsWith() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .endsWith("email", "@example.com")
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(4, users.size());
        assertTrue(users.stream().allMatch(u -> u.getEmail().endsWith("@example.com")));
    }

    @Test
    void testIn() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .in("status", List.of(User.UserStatus.ACTIVE, User.UserStatus.PENDING))
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(3, users.size());
        assertTrue(users.stream().allMatch(u ->
            u.getStatus() == User.UserStatus.ACTIVE || u.getStatus() == User.UserStatus.PENDING));
    }

    @Test
    void testBetween() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .between("age", 28, 32)
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(u -> u.getAge() >= 28 && u.getAge() <= 32));
    }

    @Test
    void testIsNull() {
        User userWithoutAddress = new User("test@example.com", "Test User", 20, User.UserStatus.ACTIVE);
        userRepository.save(userWithoutAddress);

        Specification<User> spec = SpecBuilder.forClass(User.class)
                .isNull("address")
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(1, users.size());
        assertNull(users.get(0).getAddress());
    }

    @Test
    void testIsNotNull() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .isNotNull("address")
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(4, users.size());
        assertTrue(users.stream().allMatch(u -> u.getAddress() != null));
    }

    @Test
    void testDeepPathResolution() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .eq("address.city.name", "New York")
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(u ->
            u.getAddress() != null &&
            u.getAddress().getCity() != null &&
            "New York".equals(u.getAddress().getCity().getName())));
    }

    @Test
    void testDeepPathResolutionWithContains() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .contains("address.city.name", "York")
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(u ->
            u.getAddress() != null &&
            u.getAddress().getCity() != null &&
            u.getAddress().getCity().getName().contains("York")));
    }

    @Test
    void testMultipleConditions() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .eq("status", User.UserStatus.ACTIVE)
                .greaterThan("age", 25)
                .contains("name", "Jane")
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(1, users.size());
        assertEquals("Jane Smith", users.get(0).getName());
    }

    @Test
    void testNullSafeFiltering() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .eq("email", "john.doe@example.com")
                .eq("status", (User.UserStatus) null)  // Should be skipped
                .contains("name", null)  // Should be skipped
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).getName());
    }

    @Test
    void testEmptyStringFiltering() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .contains("name", "   ")  // Should be skipped
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(4, users.size());  // All users returned
    }

    @Test
    void testInWithEmptyCollection() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .in("status", List.of())  // Should be skipped
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(4, users.size());  // All users returned
    }

    @Test
    void testComplexQuery() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .in("status", List.of(User.UserStatus.ACTIVE, User.UserStatus.PENDING))
                .gte("age", 25)
                .lte("age", 30)
                .eq("address.city.name", "New York")
                .isNotNull("email")
                .build();

        List<User> users = userRepository.findAll(spec);
        // Both John (25) and Alice (28) match: ACTIVE/PENDING, age 25-30, New York
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> "John Doe".equals(u.getName())));
        assertTrue(users.stream().anyMatch(u -> "Alice Brown".equals(u.getName())));
    }

    @Test
    void testDeepPathWithMultipleLevels() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .eq("address.city.country", "USA")
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(u ->
            u.getAddress() != null &&
            u.getAddress().getCity() != null &&
            "USA".equals(u.getAddress().getCity().getCountry())));
    }

    @Test
    void testCombinedStringOperations() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .startsWith("email", "j")
                .endsWith("email", "@example.com")
                .build();

        List<User> users = userRepository.findAll(spec);
        // Only john and jane start with "j" (bob starts with "b")
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(u ->
            u.getEmail().startsWith("j") && u.getEmail().endsWith("@example.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().startsWith("john")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().startsWith("jane")));
    }

    @Test
    void testEmptyBuilder() {
        Specification<User> spec = SpecBuilder.forClass(User.class)
                .build();

        List<User> users = userRepository.findAll(spec);
        assertEquals(4, users.size());  // All users returned
    }
}
