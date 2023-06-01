@file:Suppress("unused")

package cn.jailedbird.arouter_gradle_plugin

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.internal.plugins.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class ARouterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Only app module will use this plugin
        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            println("Init ArouterPlugin")
            val androidComponents =
                project.extensions.getByType(AndroidComponentsExtension::class.java)

            androidComponents.onVariants { variant ->
                val taskProvider =
                    project.tasks.register(
                        "${variant.name}ScanAllArouterClassTask",
                        GetAllClassesTask::class.java
                    )

                variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                    .use(taskProvider)
                    .toTransform(
                        ScopedArtifact.CLASSES,
                        GetAllClassesTask::allJars,
                        GetAllClassesTask::allDirectories,
                        GetAllClassesTask::output
                    )

            }
        }

    }
}