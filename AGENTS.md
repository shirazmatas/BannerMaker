# AI Agent Context & Guidelines (AGENTS.md)

## 📍 Project Overview

BannerMaker is a Spigot/Paper Minecraft plugin that allows players to create and manage banners through a GUI.
This project has completed modernization migration and continues to optimize core components, aiming to maintain a high-performance, modular, and easily extensible codebase.

## 🛠 Tech Stack

- **Language**: Java 21
- **Platform**: Spigot / Paper 1.21.4+
- **Build System**: Maven (Supports `minimizeJar` optimization)
- **Local Env**: `& "C:\Users\jyhsu\AppData\Local\Programs\IntelliJ IDEA Ultimate\plugins\maven\lib\maven3\bin\mvn.cmd"`
- **Key Libraries**:
    - **Adventure**: `4.17.0` (Text) / `4.3.4` (Platform Bukkit) - Core for text and message processing.
    - **ACF (Paper)**: `0.5.1-SNAPSHOT` - Command management system.
    - **InventoryFramework**: `0.10.14` - GUI interface framework.
    - **XSeries**: `11.3.0` - Cross-version material and sound compatibility.
    - **MockBukkit**: `4.41.1` - Unit testing framework (Targeting 1.21.4).

## 📐 Architecture & Patterns

### 1. Configuration and Data

- **MUST** use `club.kid7.bannermaker.configuration.ConfigManager` for all YAML file access.
- **FORBIDDEN** to directly instantiate `YamlConfiguration` or use Bukkit API's default config methods (unless inside a Manager).
- **Unit Testing**: Upon test completion (`tearDown`), `ConfigManager.reset()` **MUST** be called to clear static states and prevent cross-test contamination.
- Translation keys in `Language.java` will no longer trigger synchronous disk writes if missing at runtime to avoid performance bottlenecks.

### 2. Messaging

- **MUST** use `club.kid7.bannermaker.service.MessageService` to send messages.
- **FORBIDDEN** to use `player.sendMessage()` or `Bukkit.broadcastMessage()`.
- The `club.kid7.bannermaker.configuration.Language.tl()` method now returns an Adventure `Component`.
- **Color Code Mechanism**:
    - `Language.tl()` intelligently supports both **MiniMessage** (e.g., `<red>`, `<gradient>`) and **Legacy** (e.g., `&c`) formats.
    - If a string contains MiniMessage tags (`<` and `>`), MiniMessage parsing is prioritized; otherwise, it falls back to Legacy parsing.
- **Parameter Substitution**:
    - **Old Way (Legacy)**: `tl("key", arg1)` uses `{0}` placeholders (Not recommended, risk of injection).
    - **New Way (Recommended)**: `tl("key", TagUtil.tag("arg", value))` uses `<arg>` placeholders. Use in conjunction with `club.kid7.bannermaker.util.TagUtil` for safe escaping.
- It is recommended to use the `Language.tl(NamedTextColor color, String path, Object... args)` overloaded method to simplify the creation of colored translation Components.

### 3. ACF Integration (Command Framework)

- **Automatic Help System**: Use ACF's built-in `@HelpCommand` to automatically generate command help messages (`/bm help`).
- **Permission Filtering**: Help messages are automatically filtered based on the permissions the player possesses.
- **Language Synchronization**: ACF's system messages (e.g., "Unknown command") will automatically synchronize with the `Language` setting (`zh_TW`, `en`, `auto`) in `config.yml`. If set to `auto`, the server system language is used.

### 4. GUI Development

- **MUST** use `InventoryFramework` to implement all menus.
- GUI classes are located under the `club.kid7.bannermaker.gui` package, replacing the old `CustomGUI` system.
- If a GUI title must be a `String`, use `LegacyComponentSerializer.legacySection().serialize(component)` for conversion.

### 5. Item Building

- **MUST** use `club.kid7.bannermaker.util.ItemBuilder` to create `ItemStack` objects.
- `ItemBuilder` now supports `name(Component)`, `lore(Component...)`, and `addLore(Component...)`.
- **FORBIDDEN** to directly use `new ItemStack()`, ensuring cross-version support via XMaterial.

### 6. Utilities

- The `club.kid7.bannermaker.util.BannerUtil.isBanner()` method has been refactored to use `XTag.BANNERS` to determine if an `ItemStack` or `Material` is a banner, providing a more accurate and elegant way of judgment.

## 📝 Conventions

- **Primary Language**: Project documentation and code comments use **Traditional Chinese (正體中文)**.
- **Testing Strategy**:
    - Core logic and utility classes must include unit tests (`src/test/java`).
    - `MockBukkit` must be used to simulate the server environment.
    - Test Environment Detection: Use the `isUnitTest()` method (which checks for the MockBukkit class) to avoid initializing bStats Metrics or other unnecessary external connections during tests.
    - For testing overloaded methods with `null` parameters, please use explicit casting (e.g., `(ItemStack) null`) to avoid compilation ambiguity.

## 🗺️ Codebase Map

- `src/main/java/club/kid7/bannermaker/`
    - `BannerMaker.java`: Plugin entry point, responsible for initializing Services and Managers.
    - `configuration/`:
        - `ConfigManager.java`: Core configuration management.
        - `Language.java`: Multi-language system (Optimized, supports MiniMessage/Legacy mixing).
    - `gui/`: User interface implementation (`MainMenuGUI`, etc.).
    - `command/acf/`: ACF command handling (`BannerMakerCommand`).
    - `service/`: Core services (`MessageService`).
    - `util/`: Common utilities (`BannerUtil`, `ItemBuilder`, `TagUtil`, etc.).

## ✅ Current State

- **Migration Completed**:
    - Removed `PluginUtilities` dependency, achieving full localization.
    - Imported Adventure, ACF, InventoryFramework, XSeries.
    - Created `ConfigManager` to replace the old system.
    - Created `ItemBuilder` to replace the old `KItemStack`.
    - Rebuilt the unit testing environment and resolved testing compatibility issues between bStats and ConfigManager.
    - The language system is modernized: `Language.tl()` supports dual parsing of MiniMessage and Legacy, and supports TagResolver parameters.
    - ACF Integration: Enabled automatic Help system and implemented language setting synchronization with `config.yml`.
    - `ItemBuilder` enhanced support for `Component` type Lore.
    - `BannerUtil.isBanner` method optimized to use `XTag`, improving accuracy and elegance.
- **Known Issues/TODO**:
    - `Language.java` is still in a static singleton pattern, which poses potential risks of state pollution in unit tests (though `ConfigManager.reset()` handles most of it). Future refactoring into dependency injection might be considered.
    - **Parameter Placeholder Migration**: Gradually migrate existing `{0}` format parameter replacements to the new `TagUtil` and `TagResolver` mechanism to enhance safety and readability.
    - **ACF Command Description Localization**: Implement a mechanism to inject command descriptions (e.g., `command.description.*`) from `language/*.yml` into ACF's Locales system, allowing `/bm help` descriptions to support multi-language display.
