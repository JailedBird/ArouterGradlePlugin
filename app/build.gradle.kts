@file:Suppress("UnstableApiUsage", "SpellCheckingInspection")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("io.github.JailedBird.ARouterPlugin")
}

ksp {
    arg("AROUTER_MODULE_NAME", project.name)
}

arouter_config {
    disableTransformWhenDebugBuild = true
}

android.sourceSets.all {
    java.srcDir("build/generated/ksp/${name}/kotlin/")
}

android {
    namespace = "cn.jailedbird.arouter.ksp"
    compileSdk = 33
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "cn.jailedbird.arouter.ksp"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("arouter_gradle_plugin") {
            keyAlias = "arouter_gradle_plugin"
            keyPassword = "arouter_gradle_plugin"
            storeFile = project.rootProject.projectDir.resolve("arouter_gradle_plugin.jks")
            storePassword = "arouter_gradle_plugin"
            enableV1Signing = true
            enableV2Signing = true
        }

    }

    buildTypes {
        debug{
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("arouter_gradle_plugin")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("com.alibaba:arouter-api:1.5.2")
    ksp("com.github.JailedBird:ArouterKspCompiler:1.8.20-1.0.7")
    implementation("com.alibaba:fastjson:1.2.9")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}