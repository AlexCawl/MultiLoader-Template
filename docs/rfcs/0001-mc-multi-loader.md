# RFC 0001: mc-multi-loader MVP

## Status

Accepted for implementation. The MVP targets Minecraft 1.21.1, Java 21, Fabric, NeoForge, Mojmap, and one common dependency per loader module.

## Architecture

Root `mc-multi-loader` is an independent three-module included build:

- `common`: `dev.alexcawl.mcmultiloader.common`, shared DSL and metadata processing with no loader API dependency
- `fabric`: `dev.alexcawl.mcmultiloader.fabric`, depends only on common and Loom
- `neoforge`: `dev.alexcawl.mcmultiloader.neoforge`, depends only on common and ModDevGradle

Each plugin is applied explicitly and none of them applies Java, loader, convention, or another mc-multi-loader plugin.

Common is compiled once into a normal JAR. A loader declares exactly one `merged` project or Maven dependency. That dependency extends `implementation`, remains available to development and datagen classpaths, and its direct JAR is unpacked into the loader resource output before Fabric remapping or NeoForge packaging. Using the source-set output makes the same common classes/resources visible in dev runs and in the final JAR. Transitive dependencies are not embedded.

## Resource templates

Each resource template owns its expansion values:

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

Templates use Gradle `ProcessResources.filesMatching(...).expand(...)`. The plugin does not generate or modify loader metadata and performs no semantic validation. Loader manifests must manually register mixins and access files.

## Common descriptor and access files

The common plugin adds `META-INF/mc-multi-loader/common.properties` with schema version and optional Fabric AW / NeoForge AT resource paths. It also publishes access files as dedicated Gradle variants so Loom and ModDevGradle can consume them during configuration for both project and published module dependencies. The original resource is included in the merged JAR.

Access files are declared through `mcMultiLoader.access`, separately from metadata processing.

The MVP supports one common Fabric AW and one common NeoForge AT. It does not convert formats, aggregate loader-local files, or validate syntax.

## Packaging boundaries

The loader `processResources` consumes `zipTree(commonJar)`, and the standard `jar` consumes that source-set output. Dependency manifests, signatures, and the internal descriptor are excluded. No custom collision, service aggregation, sealed-JAR, or multi-release behavior is provided. Common and loader packages/resources are expected not to overlap.

Fabric uses Loom static mixin remapping with `useLegacyMixinAp=false`, without refmaps or the Mixin annotation processor. NeoForge gives the common AT variant to ModDevGradle. Common classes/resources are already present in the combined mod output, so the original common JAR must not also be added to `additionalRuntimeClasspath` because that would create duplicate JPMS modules.

## Out of scope

- Semantic manifest/descriptor validation
- Multiple merged dependencies
- AW/AT conversion or aggregation
- Custom duplicate handling
- Unified datagen DSL
- Minecraft versions other than 1.21.1

Validation, richer packaging, and extraction into a dedicated repository are post-MVP work.
