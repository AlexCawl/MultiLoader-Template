# RFC 0001: mc-multi-loader MVP

## Status

Accepted for implementation. The MVP targets Minecraft 1.21.1, Java 21, Fabric, NeoForge, Mojmap, and project or Maven common dependencies.

## Architecture

Root `mc-multi-loader` is an independent four-module included build:

- `core`: shared metadata DSL/model, metadata processing, descriptor entities, and merge utilities with no plugin ID
- `common`: `dev.alexcawl.mcmultiloader.common`, owns common metadata/access extension and descriptor generation
- `fabric`: `dev.alexcawl.mcmultiloader.fabric`, owns Fabric extensions and depends only on core and Loom
- `neoforge`: `dev.alexcawl.mcmultiloader.neoforge`, owns NeoForge extension and depends only on core and ModDevGradle

Each plugin is applied explicitly and none of them applies Java, loader, convention, or another mc-multi-loader plugin.

Common is compiled once into a normal JAR. A loader declares one or more `merged` project or Maven dependencies. The configuration extends `implementation` and remains available to development and datagen classpaths. Every direct JAR is unpacked into loader resources before Fabric remapping or NeoForge packaging. Descriptor-bearing transitive common JARs are unpacked too; ordinary transitive libraries stay on classpaths without embedding.

## Resource templates

Each resource template owns its expansion values:

```kotlin
mcFabricLoader {
    metadata {
        resources {
            resource("fabric.mod.json") {
                "version"(project.version)
                "mod_id"(providers.gradleProperty("mod_id"))
            }
            resource("examplemod.fabric.mixins.json") {}
            resource("pack.mcmeta") {
                "mod_name"(providers.gradleProperty("mod_name"))
            }
        }
        jarManifest {
            "Implementation-Version"(project.version)
        }
    }
}
```

Templates use Gradle `ProcessResources.filesMatching(...).expand(...)`. The plugin does not generate or modify loader metadata and performs no semantic validation. Loader manifests must manually register mixins and access files.

## Common descriptor and access files

The common plugin adds `META-INF/mc-multi-loader/common.properties` with schema version and optional Fabric AW / NeoForge AT resource paths. This descriptor works for project dependencies and published Maven modules. Original access resources remain in merged JARs.

Common access files are declared through `mcCommonLoader.access`; final Fabric AW is declared through `mcFabricLoader.access`; loader-local NeoForge AT is declared through `mcNeoForgeLoader.access`. Metadata uses the owning `mcCommonLoader`, `mcFabricLoader`, or `mcNeoForgeLoader` extension.

Each common module may expose one Fabric AW and one NeoForge AT. Fabric validates that loader-local AW contains every entry required by merged AWs without modifying it. NeoForge passes optional loader-local AT plus every extracted descriptor-declared AT to ModDevGradle. Neither loader converts formats or validates syntax.

## Packaging boundaries

The loader `processResources` consumes `zipTree(commonJar)`, and the standard `jar` consumes that source-set output. Dependency manifests, signatures, and the internal descriptor are excluded. No custom collision, service aggregation, sealed-JAR, or multi-release behavior is provided. Common and loader packages/resources are expected not to overlap.

Fabric uses Loom static mixin remapping with `useLegacyMixinAp=false`, without refmaps or the Mixin annotation processor. NeoForge gives extracted common ATs to ModDevGradle. Common classes/resources are already present in the combined mod output, so original common JARs must not also be added to `additionalRuntimeClasspath` because that would create duplicate JPMS modules.

## Out of scope

- Semantic manifest/descriptor validation
- AW/AT conversion or aggregation
- Custom duplicate handling
- Unified datagen DSL
- Minecraft versions other than 1.21.1

Validation, richer packaging, and extraction into a dedicated repository are post-MVP work.
