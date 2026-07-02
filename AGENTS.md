# Repository Guidelines

## Project Structure & Module Organization

This is a Java 21 Gradle multi-project template for Minecraft 1.21.1. Put loader-independent code in `common/src/main/java` and shared resources, mixin configuration, and access transformers in `common/src/main/resources`. Loader entry points and integrations belong in `fabric/`, `forge/`, or `neoforge/`, each using the standard `src/main/java` and `src/main/resources` layout. Keep the service interfaces in `common` and their implementations plus `META-INF/services` registrations in each loader module. Shared Gradle conventions live in `buildSrc/src/main/groovy`; versions and mod metadata are centralized in `gradle.properties`.

## Build, Test, and Development Commands

Run commands from the repository root with the checked-in wrapper:

- `./gradlew build` compiles and packages all supported loader variants.
- `./gradlew test` runs all configured test tasks (the template currently has no tests).
- `./gradlew :fabric:runClient` launches a Fabric development client.
- `./gradlew :neoforge:runClient` launches a NeoForge development client.
- `./gradlew :forge:runClient` launches a Forge development client.
- `./gradlew :neoforge:runData` regenerates NeoForge data resources.

Windows contributors should use `gradlew.bat`. Configure Gradle and the IDE to use JDK 21 before importing the project.

## Coding Style & Naming Conventions

Use four-space indentation in Java and Groovy files, braces on the same line, and existing import ordering. Java packages are lowercase (`com.example.examplemod`); classes and interfaces use PascalCase, methods and fields use camelCase, and constants use `UPPER_SNAKE_CASE`. Name mixins after their targets, such as `MixinTitleScreen`. Replace all example identifiers consistently when adopting the template, especially `mod_id`, package paths, resource filenames, and service declarations. No formatter or linter is enforced, so match nearby code and keep diffs focused.

## Testing Guidelines

Add tests under `<module>/src/test/java`, mirroring the production package, and name classes `*Test`. Prefer unit tests in `common`; use loader run configurations for integration checks. Before submitting, run `./gradlew build` and launch every loader affected by the change. Verify mixin startup logs and generated metadata when changing resources or Gradle expansion properties.

## Commit & Pull Request Guidelines

Recent commits use short, imperative, sentence-case subjects, for example `Fix DataGen in Neoforge 1.21.1`. Keep each commit scoped to one concern. Pull requests should explain the change, identify affected loaders and Minecraft versions, link relevant issues, and list commands or run configurations used for validation. Include screenshots only for visible in-game changes. Do not commit generated build output, IDE files, or local `runs/` directories.
