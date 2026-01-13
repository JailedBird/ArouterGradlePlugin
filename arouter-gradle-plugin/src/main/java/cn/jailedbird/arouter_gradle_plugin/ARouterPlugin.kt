package cn.jailedbird.arouter_gradle_plugin

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.internal.plugins.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class ARouterPlugin : Plugin<Project> {
    companion object {
        const val EXTENSION_CONFIG_NAME = "arouter_config"
    }

    override fun apply(project: Project) {
        // Only app module will use this plugin
        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            project.extensions.create(EXTENSION_CONFIG_NAME, ARouterConfig::class.java)
            println("Init ARouterGradlePlugin")
            val androidComponents =
                project.extensions.getByType(AndroidComponentsExtension::class.java)

            androidComponents.onVariants { variant ->
                val config = project.extensions.getByType(ARouterConfig::class.java)

                // 完全禁用 Transform
                if (config.disableTransform) {
                    println("Skip ARouter Transform! (disableTransform=true) variant=${variant.name}")
                    return@onVariants
                }

                // 仅 Debug 构建时禁用
                if (config.disableTransformWhenDebugBuild && variant.name.contains("debug", ignoreCase = true)) {
                    println("Skip ARouter Transform When Debug Build! variant=${variant.name}")
                    return@onVariants
                }
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

