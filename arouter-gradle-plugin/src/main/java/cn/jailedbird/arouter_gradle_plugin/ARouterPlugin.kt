package cn.jailedbird.arouter_gradle_plugin

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.internal.plugins.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class ARouterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Only app module will use this plugin
        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            println("Init ARouterGradlePlugin")
            val androidComponents =
                project.extensions.getByType(AndroidComponentsExtension::class.java)

            androidComponents.onVariants { variant ->
                val taskProviderTransformAllClassesTask =
                    project.tasks.register(
                        "${variant.name}TransformAllClassesTask",
                        TransformAllClassesTask::class.java
                    )
                // https://github.com/android/gradle-recipes
                variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                    .use(taskProviderTransformAllClassesTask)
                    .toTransform(
                        ScopedArtifact.CLASSES,
                        TransformAllClassesTask::allJars,
                        TransformAllClassesTask::allDirectories,
                        TransformAllClassesTask::output
                    )

            }
        }

    }
}