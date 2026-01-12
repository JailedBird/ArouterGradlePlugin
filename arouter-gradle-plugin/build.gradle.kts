@file:Suppress("SpellCheckingInspection")

plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("com.gradle.plugin-publish") version "1.2.0"
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

// The project version will be used as your plugin version when publishing.
group = "io.github.JailedBird"
version = "1.0.3-java21"

//pluginBundle {
//    vcsUrl = "https://github.com/JailedBird/ArouterGradlePlugin"
//    website = "https://github.com/JailedBird/ArouterGradlePlugin/blob/main/README_EN.md"
//    tags = listOf("Arouter", "AGP7", "Arouter plugin", "AGP8", "Transform", "Auto Register")
//}

gradlePlugin {

    plugins {
        register("ARouterPlugin") {
            id = "io.github.JailedBird.ARouterPlugin"
            implementationClass = "cn.jailedbird.arouter_gradle_plugin.ARouterPlugin"
            displayName = "Arouter AGP7.4+ plugin with Java 21 support"
            description = "Arouter AGP7.4+ plugin with Java 21 support (ASM 9.7)"
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
    compileOnly("com.android.tools.build:gradle:8.2.2")
    compileOnly("commons-io:commons-io:2.8.0")
    compileOnly("commons-codec:commons-codec:1.15")
    // 升级 ASM 到 9.7 支持 Java 21 (class file version 65)
    compileOnly("org.ow2.asm:asm-commons:9.7")
    compileOnly("org.ow2.asm:asm-tree:9.7")
}
