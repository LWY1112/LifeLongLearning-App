# PathLearner - Learning Management System

A JavaFX-based learning platform that allows users to enroll in courses, track progress, and advance through different learning levels.

## Features

- **User Management**: Registration, authentication, and password reset
- **Course Discovery**: Search and browse courses from Open Library API
- **Personalized Recommendations**: AI-powered course recommendations based on learner level
- **Progress Tracking**: Track enrolled and completed courses
- **Level Progression**: Automatic progression from Beginner → Intermediate → Advanced
- **Course Content**: Detailed course materials with chapters and learning objectives
- **Modern UI**: Beautiful, user-friendly interface with responsive design

## Quick Start

### Option 1: Using Maven (Recommended - Works Everywhere)
```bash
mvn clean compile javafx:run
```
**Works on Windows, macOS, and Linux!** Maven automatically handles JavaFX dependencies.

### Option 2: Using IntelliJ IDEA (Recommended - No Errors!)
1. Open project in IntelliJ IDEA
2. Wait for Maven import (automatic)
3. Run: Click run configuration dropdown → Select **"PathLearner (Maven)"** → Click play button (▶)
   - **No configuration needed - works perfectly!**

### Option 3: Using PowerShell Script (Windows Only)
**Prerequisites**: Project must be compiled first
```powershell
.\run.ps1
```

**For detailed setup instructions, see `SETUP_GUIDE.md` or `QUICK_START.md`**

## Project Configuration

- **Java Version**: 17
- **JavaFX Version**: 17.0.2
- **Main Class**: `org.example.Main`
- **Build Tool**: Maven
- **Testing Framework**: JUnit 5

## Project Structure

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── Controller/          # UI controllers
│   │   ├── model/               # Value objects and enums
│   │   ├── repository/          # Data access layer
│   │   ├── service/             # Business logic layer
│   │   ├── factory/             # Factory classes
│   │   ├── exception/           # Custom exceptions
│   │   └── [core classes]       # Course, Learner, ApiClient, etc.
│   └── resources/               # FXML files and resources
└── test/
    └── java/test/               # Unit tests
```

## Data Storage

All user data is stored in the `data/` folder:
- `user.txt` - User accounts and authentication data
- `{username}_courses.dat` - Enrolled courses for each user

## Troubleshooting

### Error: "JavaFX runtime components are missing"
- **Solution**: Use the `run.ps1` script which automatically configures the module path

### Error: "UnsupportedClassVersionError"
- **Solution**: Make sure you're using Java 17

### Error: "Classes not compiled"
- **Solution**: Build the project first:
  - IntelliJ: Build → Build Project
  - Maven: `mvn compile`

### Login Issues
- Default test account: `lwy` / `123`
- Or register a new account

## Architecture Overview

This project follows clean architecture principles with:

- **Model Layer**: Core entities (Learner, Course, CourseContent)
- **Repository Layer**: Data access interfaces and implementations
- **Service Layer**: Business logic (Enrollment, Level Progression)
- **Controller Layer**: UI controllers for JavaFX
- **Factory Pattern**: ServiceFactory for dependency creation
- **Strategy Pattern**: Recommendation strategies

For detailed architecture and code quality documentation, see:
- `CODE_QUALITY.md` - Design patterns and concepts applied
- `TESTING.md` - Unit testing documentation

## Level Progression System

Users progress through three levels by completing courses:

- **Beginner**: Start here, can access Beginner courses
- **Intermediate**: Unlock after completing 10 Beginner courses, can access Beginner and Intermediate courses
- **Advanced**: Unlock after completing 10 Intermediate courses, can access all courses

## API Integration

The application integrates with:
- **Open Library API**: For course/book metadata
- **Wikipedia API**: For educational content
- **Wikibooks API**: For structured textbook content

## Development

### Running Tests
See `TESTING.md` for detailed testing documentation.

Quick test run:
```powershell
# In IntelliJ: Right-click src/test/java/test → Run 'All Tests'
```

### Code Quality
The project follows software construction best practices:
- Encapsulation and core entity design
- Design patterns (Singleton, Factory, Strategy, Repository)
- Composition over inheritance
- Clean code principles

See `CODE_QUALITY.md` for detailed documentation.

## License

This project is part of a Software Construction course assignment.
