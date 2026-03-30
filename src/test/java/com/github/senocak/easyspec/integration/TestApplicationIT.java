package com.github.senocak.easyspec.integration;

import com.github.senocak.easyspec.builder.Operator;
import com.github.senocak.easyspec.integration.config.TestConfig;
import com.github.senocak.easyspec.integration.entity.Address;
import com.github.senocak.easyspec.integration.entity.City;
import com.github.senocak.easyspec.integration.entity.User;
import com.github.senocak.easyspec.integration.repository.UserRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
class TestApplicationIT {
    @LocalServerPort int localPort;
    private TestRestTemplate restTemplate;
    @Inject private UserRepository userRepository;

    private final FilterDto filterOrderNameAsc = new FilterDto("name", Operator.ORDER_BY, "ASC");

    @BeforeAll
    void beforeAll() {
        final RestTemplateBuilder builder = new RestTemplateBuilder().rootUri("http://localhost:" + localPort + "/");
        this.restTemplate = new TestRestTemplate(builder);
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Create cities
        final City newYork = new City("New York", null);
        final City london = new City("London", "UK");
        final City paris = new City("Paris", "France");

        // Create addresses
        final Address address1 = new Address("123 Main St", newYork);
        final Address address2 = new Address("456 Oak Ave", london);
        final Address address3 = new Address("789 Elm St", paris);
        final Address address4 = new Address("321 Pine Rd", newYork);

        // Create users
        final User user1 = new User("john.doe@example.com", "John Doe", 25, User.UserStatus.ACTIVE);
        user1.setAddress(address1);

        final User user2 = new User("jane.smith@example.com", "Jane Smith", 30, User.UserStatus.ACTIVE);
        user2.setAddress(address2);

        final User user3 = new User("bob.jones@example.com", "Bob Jones", 35, User.UserStatus.INACTIVE);
        user3.setAddress(address3);

        final User user4 = new User("alice.brown@example.com", "Alice Brown", 28, User.UserStatus.PENDING);
        user4.setAddress(address4);

        userRepository.saveAll(List.of(user1, user2, user3, user4));
    }

    @Test
    void givenEquals_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("email", Operator.EQUAL, "alice.brown@example.com");
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("alice.brown@example.com", users.getFirst().getEmail());
    }

    @Test
    void givenNotEquals_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("email", Operator.NOT_EQUAL, "not-matched-email-return-all");
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1, filterOrderNameAsc));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(4, users.size());
        assertEquals("alice.brown@example.com", users.getFirst().getEmail());
        assertEquals("bob.jones@example.com", users.get(1).getEmail());
        assertEquals("jane.smith@example.com", users.get(2).getEmail());
        assertEquals("john.doe@example.com", users.get(3).getEmail());
    }

    @Test
    void givenGraterThan_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("age", Operator.GREATER_THAN, 30);
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("bob.jones@example.com", users.getFirst().getEmail());
    }

    @Test
    void givenLessThan_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("age", Operator.LESS_THAN, 27);
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("john.doe@example.com", users.getFirst().getEmail());
    }

    @Test
    void givenGraterThanEqual_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("age", Operator.GREATER_THAN_OR_EQUAL, 30);
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1, filterOrderNameAsc));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("bob.jones@example.com", users.getFirst().getEmail());
        assertEquals("jane.smith@example.com", users.get(1).getEmail());
    }

    @Test
    void givenLessThanEqual_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("age", Operator.LESS_THAN_OR_EQUAL, 28);
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1, filterOrderNameAsc));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("alice.brown@example.com", users.getFirst().getEmail());
        assertEquals("john.doe@example.com", users.get(1).getEmail());
    }

    @Test
    void givenContains_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("email", Operator.CONTAINS, "example");
        final FilterDto filterDto2 = new FilterDto("age", Operator.GREATER_THAN, 27);
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1, filterDto2, filterOrderNameAsc));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(3, users.size());
        assertEquals("alice.brown@example.com", users.getFirst().getEmail());
        assertEquals("bob.jones@example.com", users.get(1).getEmail());
        assertEquals("jane.smith@example.com", users.get(2).getEmail());
    }

    @Test
    void givenStartsWith_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("email", Operator.STARTS_WITH, "alice");
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("alice.brown@example.com", users.getFirst().getEmail());
    }

    @Test
    void givenEndsWith_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("email", Operator.ENDS_WITH, "brown@example.com");
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("alice.brown@example.com", users.getFirst().getEmail());
    }

    @Test
    void givenIN_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("status", Operator.IN, List.of(User.UserStatus.PENDING));
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("alice.brown@example.com", users.getFirst().getEmail());
    }

    @Test
    void givenBetween_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("age", Operator.BETWEEN, 27, 29);
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("alice.brown@example.com", users.getFirst().getEmail());
    }

    @Test
    void givenIsNull_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("address.city.country", Operator.IS_NULL);
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1, filterOrderNameAsc));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("alice.brown@example.com", users.getFirst().getEmail());
        assertEquals("john.doe@example.com", users.get(1).getEmail());
    }

    @Test
    void givenIsNotNull_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterDto1 = new FilterDto("address.city.country", Operator.IS_NOT_NULL);
        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterDto1, filterOrderNameAsc));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("bob.jones@example.com", users.getFirst().getEmail());
        assertEquals("jane.smith@example.com", users.get(1).getEmail());
    }

    @Test
    void givenCombineThemAll_whenSearch_thenAssertResult() {
        // Given
        final FilterDto filterEqual = new FilterDto("email", Operator.EQUAL, "alice.brown@example.com");
        final FilterDto filterNotEqual = new FilterDto("email", Operator.NOT_EQUAL, "not-equal");
        final FilterDto filterGraterThan = new FilterDto("age", Operator.GREATER_THAN, 27);
        final FilterDto filterGraterThanEqual = new FilterDto("age", Operator.GREATER_THAN_OR_EQUAL, 28);
        final FilterDto filterLessThan = new FilterDto("age", Operator.LESS_THAN_OR_EQUAL, 29);
        final FilterDto filterLessThanEqual = new FilterDto("age", Operator.LESS_THAN_OR_EQUAL, 28);
        final FilterDto filterContains = new FilterDto("email", Operator.CONTAINS, "alice");
        final FilterDto filterStartsWith = new FilterDto("email", Operator.STARTS_WITH, "alice");
        final FilterDto filterEndsWith = new FilterDto("email", Operator.ENDS_WITH, "brown@example.com");
        final FilterDto filterIn = new FilterDto("status", Operator.IN, List.of(User.UserStatus.PENDING));
        final FilterDto filterBetween = new FilterDto("age", Operator.BETWEEN, 27, 29);
        final FilterDto filterIsNull = new FilterDto("address.city.country", Operator.IS_NULL);
        final FilterDto filterIsNoyNull = new FilterDto("address.street", Operator.IS_NOT_NULL);

        final SearchRequest request = new SearchRequest("com.github.senocak.easyspec.integration.entity.User",
            List.of(filterEqual, filterNotEqual, filterGraterThan, filterGraterThanEqual, filterLessThan,
                filterLessThanEqual, filterContains, filterStartsWith, filterEndsWith, filterIn, filterBetween, filterIsNull,
                filterIsNoyNull, filterOrderNameAsc));
        // When
        final ResponseEntity<User[]> response = restTemplate.postForEntity("/search",
            new HttpEntity<>(request), User[].class);
        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        final User[] body = response.getBody();
        assertNotNull(body);
        final List<User> users = Arrays.asList(body);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("alice.brown@example.com", users.getFirst().getEmail());
    }
}
