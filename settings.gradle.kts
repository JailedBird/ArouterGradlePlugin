@file:Suppress("UnstableApiUsage", "SpellCheckingInspection")

pluginManagement {
    repositories {
        maven(url="https://maven.aliyun.com/repository/public/")
        maven(url="https://maven.aliyun.com/repository/google/")
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
        maven(url="https://maven.aliyun.com/repository/gradle-plugin/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url="https://maven.aliyun.com/repository/public/")
        maven(url="https://maven.aliyun.com/repository/google/")
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
rootProject.name = "ArouterGradlePlugin"
include(":app")
includeBuild("arouter-gradle-plugin")
