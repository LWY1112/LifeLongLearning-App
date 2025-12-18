# Software Architecture & File Structure

This document describes the complete architecture of the PathLearner Learning Management System, including detailed file structure, module connections, and data flow.

## Architecture Overview

The application follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│    (JavaFX Controllers + FXML)      │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│          Service Layer              │
│  (Business Logic & Orchestration)   │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│        Repository Layer             │
│      (Data Access Abstraction)      │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│         Data Layer                  │
│   (File I/O, API Clients, Models)   │
└─────────────────────────────────────┘
```

## Complete File Structure

```
SC_assignment/
├── src/
│   ├── main/
│   │   ├── java/org/example/
│   │   │   ├── Main.java                          # Application entry point
│   │   │   │
│   │   │   ├── Controller/                        # UI Controllers (Presentation Layer)
│   │   │   │   ├── MainController.java           # Dashboard controller (central hub)
│   │   │   │   ├── LoginController.java          # Login page controller
│   │   │   │   ├── RegisterController.java       # Registration controller
│   │   │   │   ├── ResetPasswordController.java   # Password reset controller
│   │   │   │   ├── ProfileController.java        # User profile controller
│   │   │   │   ├── CourseController.java          # Course browsing/enrollment
│   │   │   │   ├── CourseContentController.java   # Course content display
│   │   │   │   ├── EnrollController.java          # Enrolled courses management
│   │   │   │   └── RecommendationController.java  # Course recommendations
│   │   │   │
│   │   │   ├── model/                             # Value Objects & Enums (Week 1)
│   │   │   │   ├── CourseStatus.java              # Enum: NOT_ENROLLED, ENROLLED, COMPLETED
│   │   │   │   ├── LearnerLevel.java              # Enum: BEGINNER, INTERMEDIATE, ADVANCED
│   │   │   │   └── UserProfile.java               # Value object for user profile data
│   │   │   │
│   │   │   ├── repository/                        # Repository Pattern (Week 3)
│   │   │   │   ├── ICourseRepository.java         # Interface for course data access
│   │   │   │   ├── IUserRepository.java           # Interface for user data access
│   │   │   │   ├── CourseRepository.java          # Implementation: API-based course data
│   │   │   │   └── UserRepository.java            # Implementation: File-based user data
│   │   │   │
│   │   │   ├── service/                           # Service Layer (Business Logic)
│   │   │   │   ├── IEnrollmentService.java        # Interface for enrollment operations
│   │   │   │   ├── EnrollmentService.java         # Course enrollment & completion logic
│   │   │   │   └── LevelProgressionService.java   # Level progression management
│   │   │   │
│   │   │   ├── factory/                           # Factory Pattern (Week 3)
│   │   │   │   └── ServiceFactory.java            # Singleton factory for service creation
│   │   │   │
│   │   │   ├── exception/                         # Custom Exceptions
│   │   │   │   ├── ApiException.java              # API call failures
│   │   │   │   ├── CourseEnrollmentException.java # Enrollment/completion errors
│   │   │   │   ├── DataPersistenceException.java # Data persistence errors
│   │   │   │   ├── InvalidInputException.java     # Invalid user input
│   │   │   │   ├── UserAlreadyExistsException.java# Duplicate user registration
│   │   │   │   └── UserNotFoundException.java     # User not found
│   │   │   │
│   │   │   ├── Core Entities & Classes
│   │   │   │   ├── Course.java                    # Core entity: Course model
│   │   │   │   ├── Learner.java                   # Core entity: Learner/User model
│   │   │   │   ├── CourseContent.java             # Course content model
│   │   │   │   ├── UserDatabase.java              # Singleton: User data persistence
│   │   │   │   ├── ApiClient.java                 # API client for external services
│   │   │   │   ├── RecommendationEngine.java       # Recommendation engine (Strategy pattern)
│   │   │   │   ├── RecommendationStrategy.java    # Strategy interface
│   │   │   │   └── TargetSkillStrategy.java       # Concrete strategy implementation
│   │   │   │
│   │   └── resources/                             # UI Resources
│   │       ├── login.fxml                         # Login page UI
│   │       ├── Register.fxml                     # Registration page UI
│   │       ├── forgotpassword.fxml                # Password reset page UI
│   │       ├── Dashboard.fxml                    # Main dashboard UI
│   │       ├── Profile.fxml                      # Profile page UI
│   │       ├── Course.fxml                       # Course browsing page UI
│   │       ├── CourseContent.fxml                # Course content page UI
│   │       ├── Enroll.fxml                       # Enrolled courses page UI
│   │       ├── Recommendation.fxml              # Recommendations page UI
│   │       └── Image/
│   │           └── campus.png                   # Application image
│   │
│   └── test/
│       └── java/test/                             # Unit Tests (JUnit 5)
│           ├── CourseTest.java                   # Course entity tests
│           ├── LearnerTest.java                  # Learner entity tests
│           ├── UserProfileTest.java              # UserProfile value object tests
│           ├── CourseStatusTest.java             # CourseStatus enum tests
│           ├── LearnerLevelTest.java             # LearnerLevel enum tests
│           ├── EnrollmentServiceTest.java        # Enrollment service tests
│           ├── LevelProgressionServiceTest.java  # Level progression tests
│           ├── RecommendationEngineTest.java     # Recommendation engine tests
│           ├── TargetSkillStrategyTest.java      # Strategy pattern tests
│           └── MockUserRepository.java           # Mock repository for testing
│
├── data/                                          # User Data Storage
│   ├── user.txt                                   # User accounts (CSV format)
│   ├── {username}_courses.dat                     # Enrolled courses per user (serialized)
│   ├── README.md                                  # Data folder documentation
│   └── .gitignore                                 # Git ignore rules for data
│
├── libs/                                          # External Libraries
│   └── json-20250517.jar                          # JSON parsing library
│
├── target/                                        # Maven Build Output
│   ├── classes/                                   # Compiled classes
│   └── test-classes/                             # Compiled test classes
│
├── pom.xml                                        # Maven Project Configuration
├── run.ps1                                        # PowerShell script to run application
├── run-tests.ps1                                  # PowerShell script to run tests
│
└── Documentation
    ├── README.md                                  # Software overview & quick start
    ├── CODE_QUALITY.md                            # Design patterns & concepts
    ├── TESTING.md                                 # Unit testing documentation
    └── SOFTWARE_ARCHITECTURE.md                   # This file
```

## Detailed Component Descriptions

### Entry Point
- **`Main.java`**: JavaFX application entry point
  - Initializes JavaFX application
  - Loads `login.fxml` as the initial scene
  - Sets window size and properties

### Presentation Layer (Controllers)

#### `MainController.java`
- **Role**: Central dashboard controller
- **Responsibilities**:
  - Manages navigation between pages
  - Creates and shares service instances (RecommendationEngine, EnrollmentService, CourseRepository)
  - Manages `contentPane` for dynamic content loading
  - Handles sidebar navigation buttons

#### `LoginController.java`
- **Role**: User authentication
- **Dependencies**: `UserDatabase` (Singleton)
- **Flow**: Validates credentials → Creates `Learner` object → Navigates to Dashboard

#### `RegisterController.java`
- **Role**: New user registration
- **Dependencies**: `UserDatabase` (Singleton)
- **Flow**: Validates input → Creates user account → Navigates to Login

#### `ResetPasswordController.java`
- **Role**: Password reset functionality
- **Dependencies**: `UserDatabase` (Singleton)
- **Flow**: Username verification → Email/Age verification → Password update

#### `ProfileController.java`
- **Role**: Display and edit user profile
- **Dependencies**: `Learner` object
- **Displays**: User info, level, progress, enrolled courses

#### `CourseController.java`
- **Role**: Course browsing and enrollment
- **Dependencies**: `Learner`, `IEnrollmentService`, `ICourseRepository`
- **Features**: Search courses, enroll in courses, navigate to course content

#### `CourseContentController.java`
- **Role**: Display detailed course content
- **Dependencies**: `Learner`, `Course`, `IEnrollmentService`, `ApiClient`
- **Features**: Fetch content from APIs, display chapters, mark course as completed

#### `EnrollController.java`
- **Role**: Manage enrolled courses
- **Dependencies**: `Learner`, `IEnrollmentService`
- **Features**: View enrolled courses, mark as completed, view course content

#### `RecommendationController.java`
- **Role**: Display personalized course recommendations
- **Dependencies**: `Learner`, `RecommendationEngine`
- **Features**: Get recommendations using Strategy pattern, display filtered courses

### Service Layer

#### `EnrollmentService.java`
- **Implements**: `IEnrollmentService`
- **Responsibilities**:
  - Enroll learner in course
  - Mark course as completed
  - Trigger level progression checks
  - Validate enrollment prerequisites

#### `LevelProgressionService.java`
- **Responsibilities**:
  - Calculate learner's allowed level
  - Check and upgrade learner level
  - Track progress toward next level
  - Validate level correctness

#### `ServiceFactory.java` (Singleton)
- **Pattern**: Factory + Singleton
- **Responsibilities**:
  - Create service instances
  - Create repository instances
  - Provide centralized dependency resolution
  - Ensure single instances where needed

### Repository Layer

#### `CourseRepository.java`
- **Implements**: `ICourseRepository`
- **Responsibilities**:
  - Fetch courses from Open Library API
  - Cache course data
  - Handle API errors

#### `UserRepository.java`
- **Implements**: `IUserRepository`
- **Responsibilities**:
  - Delegate to `UserDatabase` singleton
  - Provide repository interface abstraction
  - Handle user data operations

### Data Layer

#### `UserDatabase.java` (Singleton)
- **Pattern**: Singleton
- **Responsibilities**:
  - Read/write user data from `data/user.txt`
  - Manage CSV parsing
  - Handle user authentication data
  - Migrate old data files

#### `ApiClient.java`
- **Responsibilities**:
  - Fetch courses from Open Library API
  - Fetch detailed content from Wikipedia/Wikibooks
  - Generate structured course content
  - Clean and format API responses

### Core Entities

#### `Course.java`
- **Type**: Core Entity
- **Fields**: title, category, teachesSkill, level, provider, status (enum), workKey
- **Features**: Validation, type-safe status enum, serialization support

#### `Learner.java`
- **Type**: Core Entity
- **Composition**: Uses `UserProfile`, `LevelProgressionService`
- **Features**: Course enrollment, completion tracking, level management, skill tracking

#### `CourseContent.java`
- **Type**: Model
- **Fields**: title, authors, description, topics, publishDate, chapters, chapterContent
- **Purpose**: Detailed course information for display

### Model Layer (Value Objects & Enums)

#### `CourseStatus.java` (Enum)
- **Values**: NOT_ENROLLED, ENROLLED, COMPLETED
- **Purpose**: Type-safe course status management

#### `LearnerLevel.java` (Enum)
- **Values**: BEGINNER, INTERMEDIATE, ADVANCED
- **Purpose**: Type-safe level management with progression rules

#### `UserProfile.java` (Value Object)
- **Fields**: fullName, phone, email, age
- **Purpose**: Encapsulate user profile data with validation

## Application Flow

### 1. Application Startup
```
Main.java
  ↓
Loads login.fxml
  ↓
LoginController initialized
```

### 2. Authentication Flow
```
User enters credentials
  ↓
LoginController.validateLogin()
  ↓
UserDatabase.getInstance().getUser(username)
  ↓
Creates Learner object
  ↓
Loads Dashboard.fxml
  ↓
MainController initialized
```

### 3. Dashboard Navigation
```
MainController (Dashboard)
  ├─→ Profile → ProfileController
  ├─→ Courses → CourseController
  ├─→ Recommendations → RecommendationController
  ├─→ Enrolled Courses → EnrollController
  └─→ Logout → Returns to LoginController
```

### 4. Course Enrollment Flow
```
CourseController
  ↓
User searches/browses courses
  ↓
CourseRepository.fetchCourses() → ApiClient
  ↓
User clicks "Enroll"
  ↓
EnrollmentService.enrollCourse()
  ↓
Learner.enroll(course)
  ↓
Course saved to {username}_courses.dat
  ↓
Navigate to CourseContentController
```

### 5. Course Completion Flow
```
CourseContentController / EnrollController
  ↓
User clicks "Mark Completed"
  ↓
EnrollmentService.completeCourse()
  ↓
Learner.completeCourse()
  ↓
LevelProgressionService.checkAndUpgradeLevel()
  ↓
If 10 courses completed → Upgrade level
  ↓
Save level to user.txt
  ↓
Save courses to {username}_courses.dat
```

### 6. Recommendation Flow
```
RecommendationController
  ↓
RecommendationEngine.recommend(learner, courses)
  ↓
TargetSkillStrategy.recommend()
  ↓
Filter by:
  - Learner's allowed level
  - Not enrolled
  - Not completed
  - Meets prerequisites
  ↓
Return filtered recommendations (minimum 15)
```

## Module Connections

### Service Dependencies
```
MainController
  ├─→ Creates: RecommendationEngine
  ├─→ Creates: EnrollmentService (via ServiceFactory)
  ├─→ Creates: CourseRepository (via ServiceFactory)
  └─→ Passes to: All child controllers
```

### Controller Dependencies
```
CourseController
  ├─→ Receives: Learner, IEnrollmentService, ICourseRepository
  └─→ Uses: ApiClient (via CourseRepository)

EnrollController
  ├─→ Receives: Learner, IEnrollmentService
  └─→ Uses: CourseContentController (navigation)

CourseContentController
  ├─→ Receives: Learner, Course, IEnrollmentService
  └─→ Uses: ApiClient (fetch content)

RecommendationController
  ├─→ Receives: Learner, RecommendationEngine
  └─→ Uses: TargetSkillStrategy (via RecommendationEngine)
```

### Repository Dependencies
```
CourseRepository
  └─→ Uses: ApiClient

UserRepository
  └─→ Uses: UserDatabase (Singleton)
```

## Data Persistence

### User Data
- **File**: `data/user.txt`
- **Format**: CSV (username,password,fullName,phone,email,age,level)
- **Manager**: `UserDatabase` (Singleton)
- **Operations**: Read all users, add user, update password, get user

### Course Data
- **File**: `data/{username}_courses.dat`
- **Format**: Java serialization (List<Course>)
- **Manager**: `Learner.saveEnrolledCourses()` / `Learner.loadEnrolledCourses()`
- **Operations**: Save enrolled courses, load enrolled courses

## Design Patterns Applied

### 1. Singleton Pattern
- **`UserDatabase`**: Single instance for user data access
- **`ServiceFactory`**: Single instance for service creation

### 2. Factory Pattern
- **`ServiceFactory`**: Creates service and repository instances
- Centralizes object creation logic

### 3. Strategy Pattern
- **`RecommendationStrategy`**: Interface for recommendation algorithms
- **`TargetSkillStrategy`**: Concrete implementation
- **`RecommendationEngine`**: Uses strategy for flexible recommendations

### 4. Repository Pattern
- **`ICourseRepository`** / **`IUserRepository`**: Data access abstractions
- **`CourseRepository`** / **`UserRepository`**: Concrete implementations
- Enables loose coupling and testability

### 5. Composition Pattern
- **`Learner`** uses **`UserProfile`** through composition
- **`Learner`** uses **`LevelProgressionService`** through composition
- Favor composition over inheritance

## Data Flow Diagrams

### User Registration Flow
```
RegisterController
  ↓
Validate input fields
  ↓
UserRepository.addUser()
  ↓
UserDatabase.addUser()
  ↓
Write to data/user.txt
  ↓
Navigate to LoginController
```

### Course Search Flow
```
CourseController
  ↓
User enters search term
  ↓
CourseRepository.searchCourses()
  ↓
ApiClient.fetchCourses(searchTerm)
  ↓
Open Library API call
  ↓
Parse JSON response
  ↓
Return List<Course>
  ↓
Display in UI
```

### Level Progression Flow
```
Learner.completeCourse()
  ↓
Update course status to COMPLETED
  ↓
LevelProgressionService.checkAndUpgradeLevel()
  ↓
Count completed courses by level
  ↓
If Beginner: 10 completed → Upgrade to Intermediate
If Intermediate: 10 completed → Upgrade to Advanced
  ↓
Update learner level
  ↓
Save level to data/user.txt
```

## Key Design Decisions

1. **File-based Storage**: Simple CSV and serialization for user/course data
   - Easy to understand and debug
   - No database setup required
   - Suitable for small-scale application

2. **API Integration**: External APIs for course data
   - Open Library for course metadata
   - Wikipedia/Wikibooks for educational content
   - Reduces need for local content storage

3. **Layered Architecture**: Clear separation of concerns
   - Controllers handle UI logic
   - Services handle business logic
   - Repositories handle data access
   - Models represent domain entities

4. **Interface-Based Design**: Depend on abstractions
   - Controllers depend on service interfaces
   - Services depend on repository interfaces
   - Enables testing with mocks
   - Allows easy implementation swapping

5. **Singleton for Shared Resources**: UserDatabase and ServiceFactory
   - Ensures single instance
   - Centralized access
   - Reduces memory footprint

## Testing Architecture

### Test Structure
- **Location**: `src/test/java/test/`
- **Framework**: JUnit 5
- **Mocking**: `MockUserRepository` for isolated testing

### Test Coverage
- **Models**: Course, UserProfile, CourseStatus, LearnerLevel
- **Entities**: Learner
- **Services**: EnrollmentService, LevelProgressionService
- **Recommendation**: RecommendationEngine, TargetSkillStrategy

## Future Architecture Considerations

1. **Database Migration**: Replace file-based storage with database
   - Use JDBC or JPA
   - Implement database repository
   - Keep repository interfaces unchanged

2. **Dependency Injection**: Use DI framework (Guice, Spring)
   - Replace ServiceFactory with DI container
   - Automatic dependency resolution
   - Better testability

3. **Event-Driven Architecture**: Add event bus
   - Decouple components further
   - Enable reactive programming
   - Better scalability

4. **Caching Layer**: Add caching for API responses
   - Reduce API calls
   - Improve performance
   - Better user experience

## Summary

The PathLearner application follows a **clean, layered architecture** with:
- ✅ Clear separation of concerns
- ✅ Design patterns for flexibility
- ✅ Interface-based design for testability
- ✅ Composition over inheritance
- ✅ Well-organized file structure
- ✅ Comprehensive test coverage

All modules are properly connected, data flows correctly, and the architecture supports future enhancements.


