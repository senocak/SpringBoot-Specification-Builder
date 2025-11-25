# Spring Boot Easy Specification

A fluent builder library for creating Spring Data JPA Specifications easily and elegantly.

## 🎯 Purpose

This library simplifies the creation of Spring Data JPA Specifications by providing a fluent builder API. Instead of writing verbose Criteria API code, you can now build complex queries with a clean, readable syntax.

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
    <groupId>com.example</groupId>
    <artifactId>spring-boot-easy-specification</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.example:spring-boot-easy-specification:1.0.0'
```

## 🚀 Quick Start

### Basic Usage

```java
import builder.com.github.senocak.easyspec.SpecBuilder;
import org.springframework.data.jpa.domain.Specification;

// Create a specification
Specification<User> spec = SpecBuilder.forClass(User.class)
    .eq("email", "user@example.com")
    .contains("name", "John")
    .build();

// Use with repository
List<User> users = userRepository.findAll(spec);
```

### Available Methods

#### Comparison Operators

```java
SpecBuilder.forClass(User.class)
    .eq("email", email)                    // field = value
    .ne("status", "DELETED")               // field != value
    .greaterThan("age", 18)                // field > value
    .lessThan("age", 65)                   // field < value
    .gte("age", 18)                        // field >= value
    .lte("age", 65)                        // field <= value
    .build();
```

#### String Operations

```java
SpecBuilder.forClass(User.class)
    .contains("name", "john")              // field LIKE %value%
    .startsWith("email", "admin")          // field LIKE value%
    .endsWith("domain", ".com")            // field LIKE %value
    .build();
```

#### Collection Operations

```java
SpecBuilder.forClass(User.class)
    .in("status", Arrays.asList("ACTIVE", "PENDING"))  // field IN (values...)
    .build();
```

#### Range Operations

```java
SpecBuilder.forClass(User.class)
    .between("age", 18, 65)                // field BETWEEN start AND end
    .build();
```

#### Null Checks

```java
SpecBuilder.forClass(User.class)
    .isNull("deletedAt")                   // field IS NULL
    .isNotNull("email")                    // field IS NOT NULL
    .build();
```

## 🔍 Deep Path Resolution

The library automatically handles nested entity relationships and creates the necessary joins:

```java
// Automatically creates joins for nested paths
Specification<User> spec = SpecBuilder.forClass(User.class)
    .eq("address.city.name", "New York")
    .contains("address.street", "Main")
    .build();
```

This is equivalent to:
```java
Specification<User> spec = (root, query, cb) -> {
    Join<User, Address> addressJoin = root.join("address");
    Join<Address, City> cityJoin = addressJoin.join("city");
    return cb.equal(cityJoin.get("name"), "New York");
};
```

## ✨ Null-Safe Filtering

All methods are null-safe. If a value is `null`, that condition is automatically skipped:

```java
String email = null;
String name = "John";

Specification<User> spec = SpecBuilder.forClass(User.class)
    .eq("email", email)        // This condition is skipped (email is null)
    .contains("name", name)    // This condition is included
    .build();
```

## 📚 Complete Example

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<User> searchUsers(String email, String name, Integer minAge, Integer maxAge) {
        Specification<User> spec = SpecBuilder.forClass(User.class)
            .eq("email", email)
            .contains("name", name)
            .greaterThan("age", minAge)
            .lessThan("age", maxAge)
            .isNotNull("email")
            .build();
        
        return userRepository.findAll(spec);
    }
    
    public List<User> findActiveUsersInCity(String cityName) {
        Specification<User> spec = SpecBuilder.forClass(User.class)
            .eq("status", "ACTIVE")
            .eq("address.city.name", cityName)
            .build();
        
        return userRepository.findAll(spec);
    }
    
    public List<User> findUsersByStatuses(List<String> statuses) {
        Specification<User> spec = SpecBuilder.forClass(User.class)
            .in("status", statuses)
            .build();
        
        return userRepository.findAll(spec);
    }
}
```

## 🏗️ Architecture

### Core Components

- **`SpecBuilder<T>`**: The main fluent builder class
- **`Condition`**: Represents a single query condition
- **`Operator`**: Enum of supported comparison operators
- **`PathUtils`**: Utility for resolving deep field paths with automatic joins

### Class Diagram

```
SpecBuilder<T>
    ├── forClass(Class<T>)
    ├── eq(field, value)
    ├── ne(field, value)
    ├── greaterThan(field, value)
    ├── lessThan(field, value)
    ├── gte(field, value)
    ├── lte(field, value)
    ├── contains(field, value)
    ├── startsWith(field, value)
    ├── endsWith(field, value)
    ├── in(field, collection)
    ├── between(field, start, end)
    ├── isNull(field)
    ├── isNotNull(field)
    └── build() -> Specification<T>
```

## 🧪 Testing

The library includes comprehensive unit tests. Run tests with:

```bash
# Maven
mvn test

# Gradle
./gradlew test
```

## 📋 Requirements

- **Java**: 17+
- **Spring Boot**: 3.0+
- **Spring Data JPA**: 3.0+
- **Jakarta Persistence**: 3.0+

## 🔧 Building from Source

```bash
# Clone the repository
git clone https://github.com/yourusername/spring-boot-easy-specification.git
cd spring-boot-easy-specification

# Build with Maven
mvn clean install

# Build with Gradle
./gradlew build
```

## 📦 Publishing

### Maven Central

To publish to Maven Central, configure your `pom.xml` with the necessary distribution management and signing configuration.

### Local Maven Repository

```bash
mvn clean install
```

### Gradle Local Repository

```bash
./gradlew publishToMavenLocal
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Spring Data JPA team for the excellent Specification API
- The Java community for inspiration and feedback

## 📞 Support

For issues, questions, or contributions, please open an issue on GitHub.

---

**Made with ❤️ for the Spring Boot community**

