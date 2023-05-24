plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

gradlePlugin {
    plugins {
        register("ArouterGradlePlugin") {
            id = "cn.jailedbird.arouter.plugin"
            implementationClass = "cn.jailedbird.arouter_gradle_plugin.ARouterPlugin"
        }
    }
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
    gradleApi()
    compileOnly("com.android.tools.build:gradle:7.4.0")
    compileOnly("commons-io:commons-io:2.8.0")
    compileOnly("commons-codec:commons-codec:1.15")
    compileOnly("org.ow2.asm:asm-commons:9.4")
    compileOnly("org.ow2.asm:asm-tree:9.4")
}

group = "cn.jailedbird.arouter.plugin"
version = "1.0.0-beta01"

apply(from = "../gradle/maven-publish.gradle")