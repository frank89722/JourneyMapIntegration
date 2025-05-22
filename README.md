# JourneyMapIntegration [![](https://img.shields.io/modrinth/dt/M1ZKbfkJ?label=modrinth)](https://modrinth.com/mod/journeymap-integration) [![](https://cf.way2muchnoise.eu/short_525447_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/journeymap-integration)

## Overview
JourneyMapIntegration (JMI) is a Minecraft mod that enhances JourneyMap (a popular Minecraft mapping mod) with integration for other mods. The project is built using Gradle and supports multiple Minecraft mod platforms:

- Fabric
- Forge
- NeoForge

Need support? Join [JourneyMap Discord server](https://discord.gg/fRtSSxdaMG)

![JourneyMapIntegration](https://i.imgur.com/Ni9fQoo.gif)

## Features

### FTB Chunks Integration
- Display claimed chunks in JourneyMap
- Claim chunks directly from JourneyMap
- Add an Info Slot that shows claimed chunk name at your current position
- Auto-disable features that conflict with JourneyMap (MiniMap, Death waypoint, Waypoint beam)

### Waystones Integration
- Show waystones on JourneyMap as icon markers (better than waypoints especially in worlds with many waystones)

### JourneyMap Enhancements
- Default configuration shipping
- Waypoint suggestion feature (Shift right-click to send a waypoint suggestion message in client-side chat)

## Configuration
All features are toggleable in `config/jmi-client.toml`

## Project Structure

The project is organized into several modules:

- **api/** - Contains the API for compatibility modules
  - Defines interfaces and classes that compatibility modules must implement
  - Provides utility classes for working with JourneyMap

- **common/** - Contains code shared between all platforms
  - Platform-agnostic implementation of core functionality
  - Common event handling and configuration

- **compat-ftbchunks/** - FTB Chunks compatibility module
  - Implements the ModCompat interface for FTB Chunks integration
  - Provides overlays and functionality for claimed chunks

- **compat-waystones/** - Waystones compatibility module
  - Implements the ModCompat interface for Waystones integration
  - Adds waystone markers to JourneyMap

- **fabric/** - Fabric-specific implementation
  - Fabric mod initialization and platform-specific code
  - Fabric configuration handling

- **neoforge/** - NeoForge-specific implementation
  - NeoForge mod initialization and platform-specific code
  - NeoForge configuration handling

## Creating a New Compatibility Module

To create a new compatibility module for JourneyMapIntegration, follow these steps:

1. **Create a new module** in the project structure (e.g., `compat-themod/`)

2. **Configure build.gradle** for your module:
   ```gradle
   plugins {
       id 'fabric-loom' version '1.9-SNAPSHOT'
   }

   archivesBaseName = "${mod_id}-compat-yourmod-${minecraft_version}"

   dependencies {
       compileOnly project(":api")
       minecraft "com.mojang:minecraft:${minecraft_version}"
       // Add dependencies for your target mod
   }
   ```

3. **Implement the ModCompat interface**:
   ```java
   package me.frankv.jmi.compat.yourmod;

   import journeymap.api.v2.client.IClientAPI;
   import me.frankv.jmi.api.ModCompat;
   import me.frankv.jmi.api.PlatformHelper;
   import me.frankv.jmi.api.event.JMIEventBus;
   import me.frankv.jmi.api.jmoverlay.ClientConfig;
   import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;

   import java.util.Set;

   public class YourModCompat implements ModCompat {
       private final Set<ToggleableOverlay> toggleableOverlays = Set.of(/* your overlays */);
       private ClientConfig clientConfig;

       @Override
       public void init(IClientAPI jmAPI, ClientConfig clientConfig) {
           this.clientConfig = clientConfig;
           // Initialize your compatibility module
       }

       @Override
       public void registerEvent(JMIEventBus eventBus) {
           // Register event handlers
       }

       @Override
       public Set<ToggleableOverlay> getToggleableOverlays() {
           return toggleableOverlays;
       }

       @Override
       public boolean isEnabled() {
           // Check if your compatibility module is enabled in the config
           return true;
       }

       @Override
       public boolean isTargetModsLoaded() {
           // Check if your target mod is loaded
           return PlatformHelper.PLATFORM.isModLoaded("themod");
       }
   }
   ```

4. **Create any necessary overlays** by implementing the `ToggleableOverlay` interface

5. **Register your compatibility module** with Java Service Loader:
   - Create a file at `src/main/resources/META-INF/services/me.frankv.jmi.api.ModCompat`
   - Add the fully qualified name of your ModCompat implementation to this file:
     ```
     me.frankv.jmi.compat.yourmod.YourModCompat
     ```

## How Compatibility Modules are Loaded

JourneyMapIntegration uses Java Service Loader to discover and load compatibility modules:

1. **Discovery**: When the mod starts, `ModCompatFactory` uses `ServiceLoader.load(ModCompat.class)` to discover all implementations of the `ModCompat` interface.

2. **Filtering**: Discovered implementations are filtered to include only those where `isTargetModsLoaded()` returns true, ensuring that compatibility modules are only loaded if their target mods are present.

3. **Initialization**: Each compatibility module is initialized with JourneyMap's API and client configuration, and events are registered.

4. **Usage**: The factory provides a `get()` method to retrieve specific compatibility modules by class, and handles events for adding button displays for toggleable overlays.

This approach allows for a modular design where compatibility modules can be added or removed without modifying the core code.

## Event Handling System

JourneyMapIntegration implements a platform-agnostic event system that works across Fabric, Forge, and NeoForge:

1. **Platform-Specific Event Adapters**: Each platform module (fabric, forge, neoforge) contains adapters that convert platform-specific events to JMI's internal event format:
   - Fabric: Uses Fabric API events like `ClientTickEvents`, `ScreenEvents`, and `ScreenMouseEvents`
   - Forge: Uses Forge's `MinecraftForge.EVENT_BUS` and custom `ClientEventRegistry` for JourneyMap events
   - NeoForge: Uses NeoForge's `NeoForge.EVENT_BUS` for event handling

2. **Common Event Bus**: The `JMIEventBus` provides a unified event dispatching system:
   - Components can subscribe to specific event types
   - Platform adapters send events to the bus
   - The bus dispatches events to all registered handlers

3. **Event Types**: Events are defined as immutable records in the `Event` interface, including:
   - General game events (ClientTick, ScreenClose, MouseRelease)
   - JourneyMap-specific events (JMMappingEvent, JMClickEvent)
   - Custom events for JMI functionality

This architecture allows compatibility modules to work with a consistent event API regardless of the underlying mod platform.

## Design Patterns

JourneyMapIntegration is inspired by several design patterns:

1. **Adapter Pattern**: Used to convert platform-specific events from different mod loaders into a common event format that can be processed uniformly.

2. **Observer Pattern**: Implemented through the `JMIEventBus`, allowing components to subscribe to events they're interested in and be notified when those events occur.

3. **Factory Pattern**: Used in `ModCompatFactory` to create and manage compatibility modules, handling their discovery, initialization, and usage.

4. **Strategy Pattern**: The `ModCompat` interface defines a family of algorithms (compatibility strategies) that can be interchanged, allowing different compatibility modules to implement their own behavior.

5. **Service Locator Pattern**: Used with Java Service Loader to discover and load compatibility modules at runtime without hard dependencies.

## Development Guidelines

### Building the Project
To build the project, use the Gradle wrapper:
```
./gradlew build
```

This will compile the mod for all supported platforms.

### Code Style
- The project uses Lombok for code generation. Ensure that any new code follows the existing pattern of using Lombok annotations.
- Follow the existing code style in the project, including indentation and naming conventions.
- Use the existing event bus system for handling events.

### Platform-Specific Code
- Common code should be placed in the `common` module.
- Platform-specific code should be placed in the respective platform module (fabric, forge, neoforge).
- Use the existing abstraction layers to handle platform differences.
## Publishing
When making changes, consider whether they need to be documented in the changelog.md file.
