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
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
@Deprecated("This is a test file for ScanAllClassesWithTransformTask")
abstract class ScanAllClassesWithTransformTask : DefaultTask() {

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
        // ARouter plugin scan time spend 11 ms
        println("ARouter plugin scan time spend ${System.currentTimeMillis() - start} ms")

        val startNew = System.currentTimeMillis()
        JarOutputStream(output.asFile.get().outputStream()).use { jarOutput ->
            // Scan directory (Copy and Collection)
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
                            // Copy
                            file.inputStream().use { input ->
                                jarOutput.saveEntry(entryName, input)
                            }
                        }
                    }
                }
            }

            // debugCollection(targetList)
            var originInject: ByteArray? = null

            // Scan Jar, Copy & Scan & Code Inject
            val jars = allJars.get().map { it.asFile }
            for (sourceJar in jars) {
                // println("Jar file is $sourceJar")
                val jar = JarFile(sourceJar)
                val entries = jar.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    try {
                        // Exclude directory
                        if (entry.isDirectory || entry.name.isEmpty()) {
                            continue
                        }
                        // println("\tJar entry is ${entry.name}")
                        if (entry.name != ScanSetting.GENERATE_TO_CLASS_FILE_NAME) {
                            // Scan and choose
                            if (ScanUtils.shouldProcessClass(entry.name)) {
                                jar.getInputStream(entry).use { inputs ->
                                    ScanUtils.scanClass(inputs, targetList, false)
                                }
                            }
                            // Copy
                            jar.getInputStream(entry).use { input ->
                                jarOutput.saveEntry(entry.name, input)
                            }
                        } else {
                            // Skip
                            // println("Find inject byte code, Skip ${entry.name}")
                            jar.getInputStream(entry).use { inputs ->
                                originInject = inputs.readAllBytes()
                                // println("Find before originInject is ${originInject?.size}")
                            }
                        }
                    } catch (e: Exception) {
                        println("Merge jar error entry:${entry.name}, error is $e ")
                    }
                }
                jar.close()
            }
            debugCollection(targetList)
            // Do inject
            println("Start inject byte code")
            if (originInject == null) { // Check
                error("Can not find ARouter inject point, Do you import ARouter?")
            }
            val resultByteArray = InjectUtils.referHackWhenInit(
                ByteArrayInputStream(originInject), targetList
            )
            jarOutput.saveEntry(
                ScanSetting.GENERATE_TO_CLASS_FILE_NAME,
                ByteArrayInputStream(resultByteArray)
            )
            println("Inject byte code successful")
        }
        // ARouter plugin scan time spend 11 ms
        println("ARouter plugin inject time spend ${System.currentTimeMillis() - startNew} ms")
    }

    private fun JarOutputStream.saveEntry(entryName: String, inputStream: InputStream) {
        this.putNextEntry(JarEntry(entryName))
        IOUtils.copy(inputStream, this)
        this.closeEntry()
    }

    private fun debugCollection(list: List<ScanSetting>) {
        println("Collect result:")
        list.forEach { item ->
            println("[${item.interfaceName}]")
            item.classList.forEach {
                println("\t $it")
            }
        }
    }
}