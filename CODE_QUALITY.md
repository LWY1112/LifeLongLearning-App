# Code Quality & Design Concepts

This document describes the software construction concepts and design patterns applied to improve code quality, maintainability, reusability, and readability.

## Week 1 - Foundations (Encapsulation & Core Entity)

### Value Objects Created

- **`CourseStatus` enum**: Encapsulates valid course statuses (NOT_ENROLLED, ENROLLED, COMPLETED)
- **`UserProfile` class**: Value object encapsulating user profile data with validation
- **`LearnerLevel` enum**: Encapsulates learner proficiency levels (BEGINNER, INTERMEDIATE, ADVANCED)

### Core Entity Improvements

#### `Course` Class
- Uses `CourseStatus` enum instead of String for type safety
- Added validation in constructors (title cannot be null/empty)
- Added `equals()` and `hashCode()` methods
- Backward compatibility with `getStatusString()` and `setStatus(String)`
- Encapsulation: Private fields with controlled access via getters/setters

#### `Learner` Class
- Uses `UserProfile` value object through composition
- Uses `LearnerLevel` enum for level management
- Improved encapsulation with defensive copies for collections
- Added validation in setters
- Separated concerns using composition (`LevelProgressionService`)
- Delegates level progression logic to service layer

## Week 3 - Decoupling (Design Patterns)

### 1. Singleton Pattern

#### `UserDatabase`
- Private constructor prevents instantiation
- Thread-safe `getInstance()` method
- Ensures single instance for user data access
- Centralized user data management

#### `ServiceFactory`
- Singleton factory for creating services
- Centralized service creation
- Reduces coupling between controllers and service implementations
- Provides consistent service instances

### 2. Factory Pattern

#### `ServiceFactory`
- Creates and manages service instances
- Provides centralized access to repositories and services
- Allows for easy swapping of implementations
- Encapsulates object creation logic

**Usage:**
```java
IEnrollmentService enrollmentService = ServiceFactory.getEnrollmentService();
ICourseRepository courseRepository = ServiceFactory.getCourseRepository();
```

### 3. Strategy Pattern

#### `RecommendationStrategy` Interface
- Defines contract for recommendation algorithms
- Allows different recommendation strategies

#### `TargetSkillStrategy` Implementation
- Filters courses by learner's allowed level
- Excludes enrolled/completed courses
- Ensures minimum recommendations
- Assigns levels to courses without levels

#### `RecommendationEngine`
- Uses strategy pattern for flexible recommendation algorithms
- Can swap strategies without changing engine code
- Follows Open/Closed Principle (open for extension, closed for modification)

### 4. Repository Pattern

#### Interfaces
- **`ICourseRepository`**: Abstraction for course data access
- **`IUserRepository`**: Abstraction for user data access

#### Implementations
- **`CourseRepository`**: Implements `ICourseRepository` using API client
- **`UserRepository`**: Implements `IUserRepository` using UserDatabase singleton

**Benefits:**
- Loose coupling between controllers and data sources
- Easy to swap implementations (e.g., database vs file-based)
- Testable with mock repositories

### 5. Service Layer

#### Interfaces
- **`IEnrollmentService`**: Abstraction for enrollment operations

#### Implementations
- **`EnrollmentService`**: Implements enrollment and completion logic
- **`LevelProgressionService`**: Separated level progression logic from Learner entity

**Benefits:**
- Separation of concerns
- Business logic separated from entities
- Reusable across controllers

## Week 4 - Flexibility (Inheritance vs Composition)

### Composition Over Inheritance

#### `Learner` Class
- Uses `UserProfile` through composition instead of inheriting
- Uses `LevelProgressionService` through composition for level management
- Delegates level progression logic to service
- More flexible than inheritance

**Benefits:**
- Can change behavior at runtime
- Avoids deep inheritance hierarchies
- Follows "favor composition over inheritance" principle

### Interface-Based Design

#### Repository Interfaces
- `ICourseRepository`, `IUserRepository`
- Allow for easy swapping of implementations
- Example: Could swap file-based repository with database repository

#### Service Interfaces
- `IEnrollmentService`
- Allows for different implementations
- Enables dependency injection and testing

### Dependency Injection

- Controllers accept interfaces rather than concrete classes
- Services can be injected for testing and flexibility
- `ServiceFactory` provides dependency resolution
- Reduces coupling between components

## Code Quality Improvements

### 1. Encapsulation
- Private fields with controlled access
- Defensive copies for collections (prevents external modification)
- Validation in constructors and setters
- Type-safe enums instead of strings

### 2. Maintainability
- Clear separation of concerns
- Single Responsibility Principle (each class has one reason to change)
- Interfaces for loose coupling
- Factory pattern for centralized creation
- Organized package structure

### 3. Reusability
- Service classes can be reused across controllers
- Repository pattern allows different data sources
- Strategy pattern for flexible algorithms
- Factory pattern for consistent object creation

### 4. Readability
- Clear naming conventions
- JavaDoc comments on key classes
- Organized package structure:
  - `model/`: Value objects and enums
  - `repository/`: Data access interfaces and implementations
  - `service/`: Business logic services
  - `factory/`: Factory classes
  - `Controller/`: UI controllers
  - `exception/`: Custom exceptions

## Package Structure

```
org.example/
├── model/              # Value objects and enums
│   ├── CourseStatus.java
│   ├── LearnerLevel.java
│   └── UserProfile.java
├── repository/         # Data access layer
│   ├── ICourseRepository.java
│   ├── IUserRepository.java
│   ├── CourseRepository.java
│   └── UserRepository.java
├── service/            # Business logic layer
│   ├── IEnrollmentService.java
│   ├── EnrollmentService.java
│   └── LevelProgressionService.java
├── factory/            # Factory classes
│   └── ServiceFactory.java
├── exception/          # Custom exceptions
│   ├── ApiException.java
│   ├── CourseEnrollmentException.java
│   ├── DataPersistenceException.java
│   ├── InvalidInputException.java
│   ├── UserAlreadyExistsException.java
│   └── UserNotFoundException.java
└── Controller/         # UI controllers
```

## Custom Exceptions

Created specific exception classes for better error handling:
- `ApiException`: API call failures
- `CourseEnrollmentException`: Enrollment/completion failures
- `DataPersistenceException`: Data persistence errors
- `InvalidInputException`: Invalid user input
- `UserAlreadyExistsException`: Duplicate user registration
- `UserNotFoundException`: User not found

**Benefits:**
- Specific error messages
- Better error handling in controllers
- Graceful degradation

## Design Principles Applied

### SOLID Principles

1. **Single Responsibility Principle (SRP)**
   - Each class has one reason to change
   - Services handle specific business logic
   - Repositories handle data access

2. **Open/Closed Principle (OCP)**
   - Open for extension (new strategies, repositories)
   - Closed for modification (existing code unchanged)
   - Strategy pattern enables this

3. **Liskov Substitution Principle (LSP)**
   - Interface implementations are substitutable
   - Repository implementations can be swapped

4. **Interface Segregation Principle (ISP)**
   - Interfaces are focused and specific
   - No client forced to depend on unused methods

5. **Dependency Inversion Principle (DIP)**
   - Depend on abstractions (interfaces)
   - Not on concrete implementations
   - Controllers depend on service interfaces

## Benefits Summary

1. **Type Safety**: Enums prevent invalid status/level values
2. **Testability**: Interfaces allow easy mocking for unit tests
3. **Flexibility**: Easy to swap implementations (e.g., database vs file)
4. **Maintainability**: Clear separation of concerns
5. **Extensibility**: Easy to add new recommendation strategies or repositories
6. **Code Reuse**: Services can be shared across controllers
7. **Error Handling**: Custom exceptions provide specific error information
8. **Loose Coupling**: Components depend on interfaces, not implementations

## Migration Notes

### Breaking Changes
1. `Course.getStatus()` now returns `CourseStatus` enum (use `getStatusString()` for String)
2. `UserDatabase` methods are now instance methods (use `getInstance()`)
3. `EnrollmentService` and `CourseRepository` moved to new packages with interfaces

### Backward Compatibility
- `Course.getStatusString()` and `setStatus(String)` maintained for compatibility
- Controllers updated to use new structure
- ServiceFactory provides easy access to services

## Future Improvements

1. Add dependency injection framework (e.g., Guice, Spring)
2. Consider adding a database repository implementation
3. Add logging framework (e.g., SLF4J, Log4j)
4. Add validation framework (e.g., Bean Validation)
5. Add more JavaDoc comments
6. Consider adding builder pattern for complex objects
7. Add event-driven architecture for loose coupling
8. Add caching layer for API responses


