@file:Suppress("SpellCheckingInspection")

plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    id("com.gradle.plugin-publish") version "1.2.0"
}

repositories {
    maven(url="https://maven.aliyun.com/repository/public/")
    maven(url="https://maven.aliyun.com/repository/google/")
    maven(url="https://maven.aliyun.com/repository/gradle-plugin/")
    google()
    mavenCentral()
    gradlePluginPortal()
}

// The project version will be used as your plugin version when publishing.
group = "io.github.JailedBird"
version = "1.0.0-beta02"

@Suppress("UnstableApiUsage")
gradlePlugin {
    vcsUrl.set("https://github.com/JailedBird/ArouterGradlePlugin")
    website.set("https://github.com/JailedBird/ArouterGradlePlugin/blob/main/README_EN.md")

    plugins {
        register("ARouterPlugin") {
            id = "io.github.JailedBird.ARouterPlugin"
            implementationClass = "cn.jailedbird.arouter_gradle_plugin.ARouterPlugin"
            displayName = "Arouter AGP7.4+ plugin"
            description = "Arouter AGP7.4+ plugin"
            tags.set(listOf("Arouter", "AGP7", "Arouter plugin", "AGP8", "Transform", "Auto Register"))
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    gradleApi()
    compileOnly("com.android.tools.build:gradle:8.0.2")
    compileOnly("commons-io:commons-io:2.8.0")
    compileOnly("commons-codec:commons-codec:1.15")
    compileOnly("org.ow2.asm:asm-commons:9.4")
    compileOnly("org.ow2.asm:asm-tree:9.4")
}