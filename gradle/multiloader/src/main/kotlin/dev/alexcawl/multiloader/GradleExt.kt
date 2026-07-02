package dev.alexcawl.multiloader

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType

internal fun Project.mainSourceSet(): NamedDomainObjectProvider<SourceSet> {
    return extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME)
}
