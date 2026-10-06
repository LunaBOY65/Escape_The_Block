# Project Update & Refactoring Report

**Project:** Escape the Block  
**Date:** October 2026  
**Target Java Version:** Java 17 LTS (Compatible with Java 21 LTS)

---

## 1. Executive Summary

This project has been restructured and modernized from an unorganized legacy NetBeans project into a standard Maven/Gradle-compatible Java project. All source code and static assets now adhere to standard Java directory conventions, external proprietary dependencies have been eliminated, and portable build scripts have been introduced.

---

## 2. Refactoring & Changes Made

### A. Package Restructuring & Source Organization
- **Package Assignment:** Previously, all classes resided in the default (unnamed) package. All classes have been relocated into the `com.escapetheblock` package.
- **Maven/Gradle Directory Structure:**
  - Java source files moved to `src/main/java/com/escapetheblock/`.
  - Image assets (`as.png`) moved to `src/main/resources/Image/as.png`.
- **Cleaned Files & Artifacts:**
  - Removed committed binary folders (`build/`, `dist/`).
  - Removed legacy NetBeans IDE metadata (`nbproject/`, `build.xml`, `manifest.mf`, `GameStart.form`).
  - Removed corrupted root artifacts.
  - Updated `.gitignore` with comprehensive rules for Maven (`target/`), Gradle (`.gradle/`, `build/`), and modern IDEs (IntelliJ IDEA, VS Code, Eclipse).

### B. Dependency Decoupling
- **Removed NetBeans `AbsoluteLayout`:**
  - `GameStart.java` originally depended on `org.netbeans.lib.awtextra.AbsoluteLayout` (stored in `dist/lib/AbsoluteLayout.jar`).
  - Migrated UI positioning to standard Swing absolute positioning (`setLayout(null)` and `setBounds(...)`).
  - The project is now **100% pure Java SE** with **zero third-party runtime dependencies**.

### C. Build Configuration & Tooling
- **Maven (`pom.xml`):**
  - Configured compiler plugin targeting **Java 17**.
  - Configured `maven-jar-plugin` to generate an executable JAR with `Main-Class: com.escapetheblock.GameStart`.
  - Added `exec-maven-plugin` for direct execution via CLI.
- **Gradle (`build.gradle`):**
  - Added Gradle build file with Java application plugin for users preferring Gradle.

---

## 3. Reorganized Directory Structure

```
Escape_The_Block/
├── .gitignore
├── .gitattributes
├── README.md
├── update.md
├── pom.xml
├── build.gradle
├── config.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── escapetheblock/
        │           ├── Configuration.java
        │           ├── flee.java
        │           ├── GameStart.java
        │           ├── Mouse.java
        │           └── Renderer.java
        └── resources/
            └── Image/
                └── as.png
```

---

## 4. Entry Points

- **Primary Entry Point (Start Menu):** `com.escapetheblock.GameStart`
  - Launches the main welcome screen with background art and the "Start" button.
- **Direct Gameplay Entry Point:** `com.escapetheblock.flee`
  - Launches the gameplay window directly without showing the title screen.

---

## 5. How to Build and Run

### Prerequisites
- JDK 17 or higher (Java 17 LTS / Java 21 LTS)
- (Optional) Apache Maven 3.8+ or Gradle 7+

### Option 1: Using Maven
```bash
# Compile and package into an executable JAR
mvn clean package

# Run the packaged JAR
java -jar target/escape-the-block-1.0.0.jar

# Or run directly using the exec plugin
mvn exec:java
```

### Option 2: Using Gradle
```bash
# Build and run
gradle run

# Build executable JAR
gradle jar
java -jar build/libs/escape-the-block-1.0.0.jar
```

### Option 3: Using Direct JDK Tools (Without Maven/Gradle installed)
```bash
# 1. Compile
javac -d target/classes src/main/java/com/escapetheblock/*.java

# 2. Copy resources
mkdir target\classes\Image
copy src\main\resources\Image\as.png target\classes\Image\as.png

# 3. Run
java -cp target/classes com.escapetheblock.GameStart
```

---

## 6. Legacy Code Observations & Future Recommendations

1. **Naming Conventions:**
   - The `flee` class violates standard Java naming conventions (should ideally be `Flee`).
2. **File Path Handling:**
   - `Configuration.java` saves and loads `config.xml` directly in the active working directory (`./config.xml`). Using user home directory or relative app data paths could improve robustness across different execution environments.
3. **Thread Safety:**
   - Static variables (`Mouse.dragged`, `Mouse.mouseX`, `flee.best`) are shared between the Swing Event Dispatch Thread (EDT) and the custom game loop thread without synchronization or atomic wrappers.
4. **Swing Best Practices:**
   - Long-running game loops running alongside Swing can be modernized with `javax.swing.Timer` for better thread alignment with the EDT.
