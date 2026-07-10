# Repository Guidelines

## Project Structure & Module Organization

This is a Java 21 Gradle multi-project template for Minecraft 1.21.1 using Mojang mappings. Supported loaders are Fabric and NeoForge.

- `common/`: loader-independent Java code, shared resources, common mixins, Fabric AW, and NeoForge AT.
- `fabric/`: Fabric entry points, integrations, services, metadata, and native datagen configuration.
- `neoforge/`: NeoForge entry points, integrations, services, metadata, and native datagen configuration.
- `mc-multi-loader/`: independent included Gradle build containing `core`, `common`, `fabric`, and `neoforge` plugin modules.
- `gradle/convention/`: shared repository conventions.
- `gradle/libs.versions.toml`: dependency, Minecraft, loader, and plugin versions.
- `gradle.properties`: mod identity and metadata.

Keep service interfaces in `common`; put implementations and `META-INF/services` registrations in loader modules. Common code must not reference Fabric or NeoForge APIs.

## Gradle Architecture

Plugin IDs are:

- `dev.alexcawl.mcmultiloader.common`
- `dev.alexcawl.mcmultiloader.fabric`
- `dev.alexcawl.mcmultiloader.neoforge`

The implementations must remain isolated. No mc-multi-loader plugin applies Java, loader, convention, or other feature plugins; consuming projects apply all required plugins explicitly. `mc-multi-loader:core` and `mc-multi-loader:common` have no loader API dependency. Common, Fabric, and NeoForge plugin modules depend on core; Fabric and NeoForge additionally depend only on their own loader Gradle API as `compileOnly`. Do not introduce cross-loader runtime dependencies.

Keep plugin-specific extension containers and registration in their owning `common`, `fabric`, or `neoforge` module. Only shared metadata DSL/model and processing belong in `core`; `core` must not own aggregate plugin extensions.

Use typed `plugins.withType(...)` integration hooks instead of string-based `pluginManager.withPlugin(...)` callbacks. Keep loader Gradle API coordinates and test-library versions in the root version catalog. The included build imports `gradle/libs.versions.toml` for its build scripts only; do not add generated catalog accessors to plugin implementation dependencies.

Each loader module may declare multiple direct `merged` project or Maven module dependencies. `merged` extends `implementation`; every direct artifact is embedded, and transitive artifacts are embedded only when they carry the common descriptor. Do not add file dependencies.

Create custom configurations with role-locked lazy factories: `dependencyScope`, `resolvable`, or `consumable`. Preserve providers through plugin internals and avoid `configurations.create` plus mutable role flags.

Configure resource expansion and JAR manifest attributes through the owning `mcCommonLoader.metadata`, `mcFabricLoader.metadata`, or `mcNeoForgeLoader.metadata` block. Configure common AW/AT paths through `mcCommonLoader.access`, final Fabric AW through `mcFabricLoader.access`, and loader-local NeoForge AT through `mcNeoForgeLoader.access`. The plugin intentionally does not validate JSON/TOML, placeholder completeness, mixin registration, or AW/AT manifest references. Keep loader manifests explicit.

Common may publish one Fabric AW and one NeoForge AT through `fabricAccessWidener` and `neoForgeAccessTransformer`. NeoForge loader modules may additionally declare one loader-local AT. Fabric uses Loom static mixin remapping without refmaps or the legacy Mixin AP. Datagen remains native to each loader module.

## Build, Test, and Development Commands

Run commands from the repository root with the checked-in wrapper:

- `./gradlew build` compiles and packages Fabric and NeoForge.
- `./gradlew test` runs root project tests.
- `./gradlew -p mc-multi-loader clean check` runs plugin tests and Gradle plugin validation.
- `./gradlew :fabric:runClient` launches the Fabric client.
- `./gradlew :neoforge:runClient` launches the NeoForge client.
- `./gradlew :fabric:runDatagen` runs Fabric datagen.
- `./gradlew :neoforge:runData` runs NeoForge datagen.

Windows contributors should use `gradlew.bat`. Configure Gradle and the IDE to use JDK 21 before importing the project.

## Coding Style & Naming Conventions

Use four-space indentation in Java and Kotlin, braces on the same line, and existing import ordering. Java packages are lowercase (`com.example.examplemod`); classes use PascalCase, members use camelCase, and constants use `UPPER_SNAKE_CASE`. Name mixins after their targets, such as `MixinTitleScreen`.

Replace example identifiers consistently: `mod_id`, package paths, resource filenames, mixin configs, access files, and service declarations. No formatter or linter is enforced, so match nearby code and keep diffs focused.

## Testing Guidelines

Add Java unit tests under `<module>/src/test/java`, mirroring production packages, with `*Test` class names. Add Gradle plugin functional tests under the owning `mc-multi-loader/<plugin>/src/test/kotlin` module using TestKit.

Before submitting Gradle architecture changes:

1. Run `./gradlew -p mc-multi-loader clean check`.
2. Run `./gradlew build`.
3. Run datagen for each affected loader.
4. Verify affected client runs when changing mixins, AW/AT wiring, metadata, or runtime classpaths.

Keep isolation tests proving that Fabric does not require ModDevGradle and NeoForge does not require Loom. Test project and Maven `merged` dependencies when changing artifact resolution or variants.

## Commit & Pull Request Guidelines

Follow Conventional Commits using `<type>(<scope>): <description>`. Use lowercase types such as `feat`, `fix`, `docs`, `build`, or `chore`; scopes should identify the affected loader or plugin module. Write imperative descriptions and keep each commit to one concern. Mark incompatible changes with `!` and a `BREAKING CHANGE:` footer.

Pull requests should identify affected loaders and Minecraft versions, explain Gradle model changes, and list validation commands. Include screenshots only for visible changes. Do not commit build output, IDE files, or local `runs/` directories.
