@file:Suppress("UnstableApiUsage", "SpellCheckingInspection")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    // id("io.github.JailedBird.ARouterPlugin")
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
    implementation(kotlin("stdlib"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

abstract class ModifyClassesTask : DefaultTask() {

    @get:InputFiles
    abstract val allClasses: ListProperty<Directory>

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    // @get:OutputFiles
    // abstract val output: DirectoryProperty

    @TaskAction
    fun taskAction() {

        allClasses.get().forEach { directory ->
            println("Directory : ${directory.asFile.absolutePath}")
            directory.asFile.walk().filter(File::isFile).forEach { file ->
                println("${file.absoluteFile}")
                /*if (file.name == "SomeSource.class") {
                    println("File : ${file.absolutePath}")
                    val interfaceClass = pool.makeInterface("com.android.api.tests.SomeInterface");
                    println("Adding $interfaceClass")
                    interfaceClass.writeFile(output.get().asFile.absolutePath)
                    java.io.FileInputStream(file).use {
                        val ctClass = pool.makeClass(it);
                        ctClass.addInterface(interfaceClass)
                        val m = ctClass.getDeclaredMethod("toString");
                        if (m != null) {
                            m.insertBefore("{ System.out.println(\"Some Extensive Tracing\"); }");
                        }
                        ctClass.writeFile(output.get().asFile.absolutePath)
                    }
                }*/
            }
        }

        allJars.get().forEach {
            println("Jar : ${it.asFile.absolutePath}")
        }
    }
}

abstract class ModifyClassesTask1 : DefaultTask() {

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:OutputFiles
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        allJars.get().forEach {
            println("${it.asFile.absoluteFile}")
        }
    }
}

androidComponents {
    onVariants { variant ->
        // val taskProvider = project.tasks.register<ModifyClassesTask>("${variant.name}ModifyClasses"){
        //     allClasses.set(variant.artifacts.getAll(com.android.build.api.artifact.MultipleArtifact.ALL_CLASSES_DIRS))
        //     allJars.set(variant.artifacts.getAll(com.android.build.api.artifact.MultipleArtifact.ALL_CLASSES_JARS))
        // }
        //
        // // variant.artifacts.use<ModifyClassesTask>(taskProvider)
        // //     .wiredWith(ModifyClassesTask::allClasses, ModifyClassesTask::output)
        // //     .toTransform(com.android.build.api.artifact.MultipleArtifact.ALL_CLASSES_DIRS)
        //
        // val taskProvider1 = project.tasks.register<ModifyClassesTask1>("${variant.name}ModifyClasses1"){
        //     dependsOn(taskProvider)
        // }
        // variant.artifacts.use<ModifyClassesTask1>(taskProvider1)
        //     .wiredWith(ModifyClassesTask1::allJars, ModifyClassesTask1::output)
        //     .toTransform(com.android.build.api.artifact.MultipleArtifact.ALL_CLASSES_JARS)
        //

    }
}