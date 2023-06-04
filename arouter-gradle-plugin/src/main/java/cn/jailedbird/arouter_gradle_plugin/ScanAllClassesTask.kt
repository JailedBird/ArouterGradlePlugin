package cn.jailedbird.arouter_gradle_plugin

import cn.jailedbird.arouter_gradle_plugin.utils.ScanSetting
import cn.jailedbird.arouter_gradle_plugin.utils.ScanUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ScanAllClassesTask : DefaultTask() {

    companion object {
        private const val TAG = "ScanAllClassesTask"
    }

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @TaskAction
    fun taskAction() {
        val leftSlash = File.separator == "/"
        val targetList: List<ScanSetting> = listOf(
            ScanSetting("IRouteRoot"),
            ScanSetting("IInterceptorGroup"),
            ScanSetting("IProviderGroup"),
        )
        val start = System.currentTimeMillis()

        // Scan directories
        allDirectories.get().forEach { directory ->
            val directoryPath =
                if (directory.asFile.absolutePath.endsWith(File.separatorChar)) {
                    directory.asFile.absolutePath
                } else {
                    directory.asFile.absolutePath + File.separatorChar
                }
            // println("Directory is $directoryPath")
            directory.asFile.walk().forEach { file ->
                if (file.isFile) {
                    val entryName = if (leftSlash) {
                        file.path.substringAfter(directoryPath)
                    } else {
                        file.path.substringAfter(directoryPath).replace(File.separatorChar, '/')
                    }
                    // println("\tDirectory entry name $entryName")
                    if (entryName.isNotEmpty()) {
                        // Use stream to detect register, Take care, stream can only be read once,
                        // So, When Scan and Copy should open different stream;
                        if (ScanUtils.shouldProcessClass(entryName)) {
                            file.inputStream().use { input ->
                                ScanUtils.scanClass(input, targetList, false)
                            }
                        }
                    }
                }
            }
        }

        // Scan jars
        val scanJars = allJars.get().map { it.asFile }
        for (jar in scanJars) {
            // Scan jar(exclude white list); TODO check jar path rules
            // if (ScanUtils.shouldProcessPreDexJar(jar.absolutePath)) {
            //     ScanUtils.scanJar(jar, targetList)
            // }
            println(jar.absolutePath)
            ScanUtils.scanJar(jar, targetList)
        }

        debugCollection(targetList)
        // ARouter plugin scan time spend 11 ms
        println("ARouter plugin scan time spend ${System.currentTimeMillis() - start} ms")
    }

    private fun debugCollection(list: List<ScanSetting>) {
        println("$TAG collect result:")
        list.forEach { item ->
            println("[${item.interfaceName}]")
            item.classList.forEach {
                println("\t $it")
            }
        }
    }
}