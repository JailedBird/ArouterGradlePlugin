package cn.jailedbird.arouter_gradle_plugin

import cn.jailedbird.arouter_gradle_plugin.utils.InjectUtils
import cn.jailedbird.arouter_gradle_plugin.utils.ScanSetting
import cn.jailedbird.arouter_gradle_plugin.utils.ScanUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayInputStream
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * TODO LIST
 * 1. closeEntry perhaps cause performance problem, Please use ZipEntry to optmize
 * 2. Please test build result, can run safely
 * */
abstract class GetAllClassesTask : DefaultTask() {

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val leftSlash = File.separator == "/"
        val targetList: List<ScanSetting> = listOf(
            ScanSetting("IRouteRoot"),
            ScanSetting("IInterceptorGroup"),
            ScanSetting("IProviderGroup"),
        )

        JarOutputStream(output.asFile.get().outputStream()).use { jarOutput ->
            // Scan directoy (Copy and Collection)
            allDirectories.get().forEach { directory ->
                val directoryPath =
                    if (directory.asFile.absolutePath.endsWith(File.separatorChar)) {
                        directory.asFile.absolutePath
                    } else {
                        directory.asFile.absolutePath + File.separatorChar
                    }
                println("Directory is $directoryPath")
                directory.asFile.walk().forEach { file ->
                    if (file.isFile) {
                        val entryName = if (leftSlash) {
                            file.path.substringAfter(directoryPath)
                        } else {
                            file.path.substringAfter(directoryPath).replace(File.separatorChar, '/')
                        }
                        println("\tDirectory entry name $entryName")
                        if (entryName.isNotEmpty()) {
                            // Use stream to detect register, Take care, stream can only be read once,
                            // So, When Scan and Copy should open different stream;
                            if (ScanUtils.shouldProcessClass(entryName)) {
                                file.inputStream().use { input ->
                                    ScanUtils.scanClass(input, targetList, false)
                                }
                            }
                            // Copy
                            val entry = JarEntry(entryName)
                            jarOutput.putNextEntry(entry)
                            file.inputStream().use { input ->
                                input.copyTo(jarOutput)
                            }
                            jarOutput.closeEntry()
                        }
                    }
                }
            }

            debugCollection(targetList)
            var originInject: ByteArray? = null
            // Scan Jar, Copy & Scan & Code Inject
            val jars = allJars.get().map { it.asFile }
            for (sourceJar in jars) {
                println("Jar file is $sourceJar")
                val jar = JarFile(sourceJar)
                val entries = jar.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    try {
                        // Exclude directory
                        if (entry.isDirectory || entry.name.isEmpty()) {
                            continue
                        }
                        println("\tJar entry is ${entry.name}")
                        if (entry.name != ScanSetting.GENERATE_TO_CLASS_FILE_NAME) {
                            // Scan and choose
                            if (ScanUtils.shouldProcessClass(entry.name)) {
                                jar.getInputStream(entry).use { inputs ->
                                    ScanUtils.scanClass(inputs, targetList, false)
                                }
                            }
                            // Copy
                            jarOutput.putNextEntry(JarEntry(entry.name))
                            jar.getInputStream(entry).use { inputs ->
                                IOUtils.copy(inputs, jarOutput)
                            }
                            jarOutput.closeEntry()
                        } else {
                            // Skip
                            println("Find inject byte code, Skip ${entry.name}")
                            jar.getInputStream(entry).use { inputs ->
                                originInject = inputs.readAllBytes()
                                println("Find befor originInject is ${originInject?.size}")
                            }
                        }
                    } catch (e: Exception) {
                        println("Merge jar error entry:${entry.name}, error is $e ")
                    }
                }
                jar.close()
            }
            debugCollection(targetList)
            // Skip
            println("Start inject byte code")
            if (originInject == null) { // Check
                error("Can not find arouter inject point, Do you import arouter?")
            }
            val resultByteArray = InjectUtils.referHackWhenInit(
                ByteArrayInputStream(originInject), targetList
            )
            jarOutput.putNextEntry(JarEntry(ScanSetting.GENERATE_TO_CLASS_FILE_NAME))
            IOUtils.copy(ByteArrayInputStream(resultByteArray), jarOutput)
            jarOutput.closeEntry()
        }
    }

    private fun debugCollection(list: List<ScanSetting>) {
        println("Result:")
        list.forEach { item ->
            println("\n[${item.interfaceName}]")
            item.classList.forEach {
                println("\t $it")
            }
        }
    }
}