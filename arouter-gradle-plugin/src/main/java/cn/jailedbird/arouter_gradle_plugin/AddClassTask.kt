package cn.jailedbird.arouter_gradle_plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction


abstract class AddClassesTask: DefaultTask() {

    @get:OutputFiles
    abstract val output: DirectoryProperty

    @TaskAction
    fun taskAction() {
        println("Fuck AddClassesTask task execute!")
        val start = System.currentTimeMillis()
        println("ARouter plugin scan time spend ${System.currentTimeMillis() - start} ms")
        /*val pool = ClassPool(ClassPool.getDefault())


        val interfaceClass = pool.makeInterface("com.android.api.tests.SomeInterface");
        println("Adding $interfaceClass")
        interfaceClass.writeFile(output.get().asFile.absolutePath)*/
    }
}