# MultiLoader Template

Gradle template for Minecraft 1.21.1 mods targeting Fabric and NeoForge from one shared codebase. The project uses Java 21, Mojang mappings, Fabric Loom, ModDevGradle, and the included `mc-multi-loader` plugin build.

## Project layout

```text
common/                 loader-independent code and resources
fabric/                 Fabric entry points and integrations
neoforge/               NeoForge entry points and integrations
mc-multi-loader/        Gradle plugins used by the project modules
gradle/convention/      shared repository conventions
gradle/libs.versions.toml
gradle.properties       mod metadata
```

The included plugin build is split by loader. A project only loads the implementation it applies:

- `dev.alexcawl.mcmultiloader.common`
- `dev.alexcawl.mcmultiloader.fabric`
- `dev.alexcawl.mcmultiloader.neoforge`

The plugins are independent and never apply Java, loader, or other convention plugins. Apply the required language and loader plugins explicitly before configuring their extensions.

## Getting started

1. Clone the repository.
2. Set the mod ID, name, version, package, authors, and other metadata in `gradle.properties`.
3. Update Minecraft, loader, API, and Gradle plugin versions in `gradle/libs.versions.toml`.
4. Rename the example Java packages, resource files, mixin configs, service declarations, and `rootProject.name` consistently.
5. Import the repository root into IntelliJ IDEA with JDK 21 selected for both the project SDK and Gradle JVM.
6. Reload Gradle and run either the Fabric or NeoForge client configuration.

Eclipse is not supported.

## Architecture

Most code belongs in `common`. It may use Minecraft, Mixin, and loader-independent libraries, but it must not reference Fabric or NeoForge APIs. Loader-specific entry points, event handling, integrations, and service implementations belong in their respective modules.

Each loader module declares one or more common artifacts:

```kotlin
dependencies {
    merged(project(":common"))
}
```

`merged` also accepts normal Maven module dependencies. It behaves like `implementation` for compile and runtime classpaths. Direct `merged` artifacts are embedded; transitive artifacts are embedded only when they contain `META-INF/mc-multi-loader/common.properties`.

Common classes and resources are processed together with loader output. Fabric Loom therefore remaps common and Fabric classes in one pass; NeoForge packages the same common output through ModDevGradle.

## Resources and metadata

Resource expansion is configured per file:

```kotlin
mcMultiLoader {
    metadata {
        template {
            template("fabric.mod.json") {
                "version"(project.version)
                "mod_id"(providers.gradleProperty("mod_id"))
            }
            template("examplemod.fabric.mixins.json") {}
            template("pack.mcmeta") {
                "mod_name"(providers.gradleProperty("mod_name"))
            }
        }
        jarManifest {
            "Implementation-Version"(project.version)
        }
    }
}
```

The plugin expands declared files but does not generate or validate Fabric JSON, NeoForge TOML, mixin declarations, or placeholders. Loader manifests remain explicit source files.

The common module may declare one Fabric access widener and one NeoForge access transformer:

```kotlin
mcMultiLoader {
    access {
        fabricAccessWidener("accesswidener")
        neoForgeAccessTransformer("META-INF/accesstransformer.cfg")
    }
}
```

The descriptor in the common JAR is the source used by loader plugins to discover access files.

Fabric loader modules own their final access widener and configure it through the Fabric extension:

```kotlin
mcFabricLoader {
    access {
        fabricAccessWidener("src/main/resources/META-INF/fabric.accesswidener")
    }
}
```

The Fabric plugin wires that file into Loom and validates that it contains the entries required by all embedded common access wideners. Fabric metadata must reference the same resource path explicitly. NeoForge extracts all descriptor-declared access transformers from embedded common artifacts and passes them to ModDevGradle.

Fabric mixins use Loom static remapping. Refmaps, the legacy Mixin annotation processor, and `loom.mixin` are not used.

## Commands

Run commands from the repository root:

```shell
./gradlew build
./gradlew test
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
./gradlew :fabric:runDatagen
./gradlew :neoforge:runData
./gradlew -p mc-multi-loader check
```

Generated data is written to each loader module's `src/generated/resources` directory. Datagen is configured natively per loader; `mc-multi-loader` does not create a shared datagen abstraction.

The plugin design and MVP boundaries are documented in [RFC 0001](docs/rfcs/0001-mc-multi-loader.md).
