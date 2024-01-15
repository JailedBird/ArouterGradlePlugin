@file:Suppress("SpellCheckingInspection")

plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("com.gradle.plugin-publish") version "1.2.0"
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

// The project version will be used as your plugin version when publishing.
group = "io.github.JailedBird"
version = "1.0.1"

pluginBundle {
    vcsUrl = "https://github.com/JailedBird/ArouterGradlePlugin"
    website = "https://github.com/JailedBird/ArouterGradlePlugin/blob/main/README_EN.md"
    tags = listOf("Arouter", "AGP7", "Arouter plugin", "AGP8", "Transform", "Auto Register")
}
gradlePlugin {

    plugins {
        register("ARouterPlugin") {
            id = "io.github.JailedBird.ARouterPlugin"
            implementationClass = "cn.jailedbird.arouter_gradle_plugin.ARouterPlugin"
            displayName = "Arouter AGP7.4+ plugin"
            description = "Arouter AGP7.4+ plugin"
        }
    }
}

/*gradlePlugin {
    website.set("https://github.com/JailedBird/ArouterGradlePlugin")
    vcsUrl.set("https://github.com/JailedBird/ArouterGradlePlugin/README_EN.md")
    plugins {
        create("greetingsPlugin") {
            id = "io.github.johndoe.greeting"
            implementationClass = "io.github.johndoe.gradle.GreetingPlugin"
            displayName = "Gradle Greeting plugin"
            description = "Gradle plugin to say hello!"
            tags.set(listOf("search", "tags", "for", "your", "hello", "plugin"))
        }
        create("goodbyePlugin") {
            id = "io.github.johndoe.goodbye"
            implementationClass = "io.github.johndoe.gradle.GoodbyePlugin"
            displayName = "Gradle Goodbye plugin"
            description = "Gradle plugin to say goodbye!"
            tags.set(listOf("search", "tags", "for", "your", "goodbye", "plugin"))
        }
    }
}*/

dependencies {
    implementation(kotlin("stdlib"))
    gradleApi()
    compileOnly("com.android.tools.build:gradle:7.4.0")
    compileOnly("commons-io:commons-io:2.8.0")
    compileOnly("commons-codec:commons-codec:1.15")
    compileOnly("org.ow2.asm:asm-commons:9.4")
    compileOnly("org.ow2.asm:asm-tree:9.4")
}
