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

android.sourceSets.all {
    java.srcDir("build/generated/ksp/${name}/kotlin/")
}

android {
    namespace = "cn.jailedbird.arouter.ksp"
    compileSdk = 33

    defaultConfig {
        applicationId = "cn.jailedbird.arouter.ksp"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("com.alibaba:arouter-api:1.5.2")
    ksp("com.github.JailedBird:ArouterKspCompiler:1.6.10-1.0.2")
    implementation("com.alibaba:fastjson:1.2.9")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}