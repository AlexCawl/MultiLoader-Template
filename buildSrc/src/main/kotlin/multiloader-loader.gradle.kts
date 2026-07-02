import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.bundling.Jar

plugins {
    id("multiloader-common")
}

val modId = providers.gradleProperty("mod_id").get()

val commonJava by configurations.creating {
    isCanBeResolved = true
}
val commonResources by configurations.creating {
    isCanBeResolved = true
}

dependencies {
    compileOnly(project(":common")) {
        capabilities {
            requireCapability("$group:$modId")
        }
    }
    commonJava(project(path = ":common", configuration = "commonJava"))
    commonResources(project(path = ":common", configuration = "commonResources"))
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.processResources {
    dependsOn(commonResources)
    from(commonResources)
}

tasks.named<Javadoc>("javadoc") {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(commonJava)
    from(commonJava)
    dependsOn(commonResources)
    from(commonResources)
}
