# Spring Boot Easy Specification

A fluent builder library for creating Spring Data JPA Specifications easily and elegantly. It simplifies the creation of dynamic queries by providing a clean, readable syntax for building `Specification<T>` objects.

## 🎯 Purpose

This library simplifies the creation of Spring Data JPA Specifications by providing a fluent builder API. Instead of writing verbose and hard-to-maintain Criteria API code, you can now build complex queries with a clean, readable syntax.

### Before (Traditional Approach)

```java
Specification<User> spec = Specification.where(
    (root, query, cb) -> cb.equal(root.get("email"), email)
).and(
    (root, query, cb) -> cb.like(root.get("name"), "%" + name + "%")
);
```

### After (With Easy Specification)

```java
Specification<User> spec = SpecBuilder.forClass(User.class)
    .eq("email", email)
    .contains("name", name)
    .greaterThan("age", 18)
    .build();
```

## 📦 Installation

### Maven

```xml
<dependency>
    <groupId>com.github.senocak</groupId>
    <artifactId>spring-boot-easy-specification</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.github.senocak:spring-boot-easy-specification:1.0.1'
```

## 🚀 Quick Start

### Basic Usage

```java
import com.github.senocak.easyspec.builder.SpecBuilder;
import org.springframework.data.jpa.domain.Specification;

// Create a specification
Specification<User> spec = SpecBuilder.forClass(User.class)
    .eq("email", "user@example.com")
    .contains("name", "John")
    .orderBy("name", Sort.Direction.ASC)
    .build();

// Use with repository
List<User> users = userRepository.findAll(spec);
```

## 🔍 Available Methods

The `SpecBuilder` supports a wide range of comparison operators and string operations.

### Comparison Operators

| Method | Operator | Description |
|--------|----------|-------------|
| `.eq(field, value)` | `=` | Equals |
| `.ne(field, value)` | `!=` | Not Equals |
| `.greaterThan(field, value)` | `>` | Greater Than |
| `.lessThan(field, value)` | `<` | Less Than |
| `.gte(field, value)` | `>=` | Greater Than or Equal |
| `.lte(field, value)` | `<=` | Less Than or Equal |
| `.between(field, start, end)` | `BETWEEN` | Range check |
| `.in(field, collection)` | `IN` | Check membership in a collection |
| `.isNull(field)` | `IS NULL` | Check if field is null |
| `.isNotNull(field)` | `IS NOT NULL` | Check if field is not null |

### String Operations

| Method | SQL Equivalent | Description |
|--------|----------------|-------------|
| `.contains(field, value)` | `LIKE %value%` | Substring search |
| `.startsWith(field, value)` | `LIKE value%` | Prefix search |
| `.endsWith(field, value)` | `LIKE %value` | Suffix search |

### Sorting Operations

| Method                 | SQL Equivalent | Description    |
|------------------------|---------------|----------------|
| `.orderBy(field)`      | `order by`    | Order function |

## 🔍 Deep Path Resolution

The library automatically handles nested entity relationships and creates the necessary joins. You can use dot notation to access fields in related entities.

```java
// Automatically creates joins for nested paths
Specification<User> spec = SpecBuilder.forClass(User.class)
    .eq("address.city.name", "New York")
    .contains("address.street", "Main")
    .build();
```

This is equivalent to writing complex criteria code with multiple joins:
```java
Specification<User> spec = (root, query, cb) -> {
    Join<User, Address> addressJoin = root.join("address", JoinType.LEFT);
    Join<Address, City> cityJoin = addressJoin.join("city", JoinType.LEFT);
    return cb.equal(cityJoin.get("name"), "New York");
};
```

## ✨ Null-Safe Filtering

All methods are null-safe. If a value is `null`, that condition is automatically skipped. This is particularly useful for building dynamic search queries from optional request parameters.

```java
String email = null;
String name = "John";

Specification<User> spec = SpecBuilder.forClass(User.class)
    .eq("email", email)        // This condition is automatically skipped
    .contains("name", name)    // This condition is included
    .build();
```

## 🛠️ Dynamic Query Building (REST API Example)

You can use the `SpecBuilder` to dynamically create queries based on incoming search requests:

```java
@PostMapping("/search")
public List<User> search(@RequestBody SearchRequest request) {
    SpecBuilder<User> specBuilder = SpecBuilder.forClass(User.class);
    
    for (FilterDto filter : request.filters()) {
        switch (filter.operator()) {
            case EQUAL -> specBuilder.eq(filter.field(), filter.value());
            case CONTAINS -> specBuilder.contains(filter.field(), (String) filter.value());
            // ... map other operators
            case ORDER_BY -> specBuilder.orderBy(filter.field(), (Sort.Direction) filter.value());
        }
    }
    
    return userRepository.findAll(specBuilder.build());
}
```

## 🏗️ Architecture

- **`SpecBuilder<T>`**: The fluent API entry point for building JPA Specifications.
- **`Condition`**: Internal representation of a single query criteria (field, operator, values).
- **`Operator`**: Enum defining the supported operations (EQUAL, CONTAINS, BETWEEN, etc.).

## 🧪 Requirements

- **Java**: 25
- **Spring Boot**: 3.5.12
- **Spring Data JPA**: 3.5.10

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Made with ❤️ for the Spring Boot community**

