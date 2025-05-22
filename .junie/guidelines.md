# JourneyMapIntegration Project Guidelines

## Project Overview
JourneyMapIntegration (JMI) is a Minecraft mod that enhances JourneyMap (a popular Minecraft mapping mod) with integration for other mods. The project is built using Gradle and supports multiple Minecraft mod platforms:

- Fabric
- Forge
- NeoForge

### Current Features
- **FTB Chunks Integration**
  - Display and manage claimed chunks in JourneyMap
  - Auto-disable features that conflict with JourneyMap

- **Waystones Integration**
  - Show waystones on JourneyMap as icon markers

- **JourneyMap Enhancements**
  - Default configuration shipping
  - Waypoint suggestion feature

## Project Structure
- **api/** - Contains the API for compat modules
- **common/** - Contains code shared between all platforms
- **compat-ftbchunks/** - FTB Chunks compatibility module
- **compat-waystones/** - Waystones compatibility module
- **fabric/** - Fabric-specific implementation
- **forge/** - Forge-specific implementation
- **neoforge/** - NeoForge-specific implementation

## Development Guidelines

### Building the Project
To build the project, use the Gradle wrapper:
```
./gradlew build
```

This will compile the mod for all supported platforms.

### Running Tests
Before submitting changes, Junie should run tests to verify that the changes don't break existing functionality. Tests can be run with:
```
./gradlew test
```

### Code Style
- The project uses Lombok for code generation. Ensure that any new code follows the existing pattern of using Lombok annotations.
- Follow the existing code style in the project, including indentation and naming conventions.
- Use the existing event bus system for handling events.

### Platform-Specific Code
- Common code should be placed in the `common` module.
- Platform-specific code should be placed in the respective platform module (fabric, forge, neoforge).
- Use the existing abstraction layers to handle platform differences.

## Versioning
The project follows a versioning scheme of `<minecraft_version>-<mod_version>`. For example, `1.21.1-1.9` indicates version 1.9 of the mod for Minecraft 1.21.1.

## Publishing
The mod is published on:
- CurseForge (ID: 525447)
- Modrinth (ID: M1ZKbfkJ)

When making changes, consider whether they need to be documented in the changelog.md file.
