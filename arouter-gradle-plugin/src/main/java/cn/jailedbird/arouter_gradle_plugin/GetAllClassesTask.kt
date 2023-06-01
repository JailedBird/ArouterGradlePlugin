package cn.jailedbird.arouter_gradle_plugin

import cn.jailedbird.arouter_gradle_plugin.utils.RegisterTransform
import cn.jailedbird.arouter_gradle_plugin.utils.ScanSetting
import cn.jailedbird.arouter_gradle_plugin.utils.ScanUtil
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
 * 2. Perhaps we can only scan & copy(or code inject) in one turn, avoid twice scan jar
 * 3. Optimize file path resolve function, make it graceful
 * 3. Please test build result, can run safely
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
        val start = System.currentTimeMillis()
        val leftSlash = File.separator == "/"
        allDirectories.get().forEach { directory ->
            println("Directory : ${directory.asFile.absolutePath}")
            val root = directory.asFile.absolutePath
            directory.asFile.walk().filter(File::isFile).forEach { file ->
                val path = file.absolutePath.replace(root, "")
                // TODO optimize code
                val processedPath =
                    (if (leftSlash) path else path.replace("\\", "/")).trimStart('/')
                if (file.isFile && ScanUtil.shouldProcessClass(processedPath)) {
                    ScanUtil.scanClass(file)
                }
            }
        }

        allJars.get().forEach { file ->
            println("JarFile : ${file.asFile.absolutePath}")
            if (ScanUtil.shouldProcessPreDexJar(file.asFile.absolutePath)) {
                ScanUtil.scanJar(file.asFile)
            }
        }

        debugCollection(RegisterTransform.registerList)
        println("Scan ARouter class use ${System.currentTimeMillis() - start}ms")

        // 先对所有Class进行扫描，扫描完毕再寻找遍历Jar寻找对应的注入点[暂时不要去考虑优化问题]
        allJars.get().forEach { file ->
            // println("JarFile : ${file.asFile.absolutePath}")
            if (ScanUtil.shouldProcessPreDexJar(file.asFile.absolutePath)) {
                ScanUtil.scanJar(file.asFile)
            }
        }
        // 理论上来说，Arouter核心类只能在Jar中出现， 不能出现在目录中 因此 搜索和查找变换均限定在Jar
        // println("Find arouter in jar ${RegisterTransform.injectJarName}")
        // Get jar [寻找对应的JAR]
        /*val jar = allJars.get()
            .firstOrNull { it.asFile.absolutePath == RegisterTransform.injectJarName }?.asFile
            ?: error("Can not find inject point, Please import")*/

        JarOutputStream(output.asFile.get().outputStream()).use { jarOutput ->
            /*allJars.get().forEach { jarRegularFile ->
                val jarFile = jarRegularFile.asFile
                if (jarFile.absolutePath == RegisterTransform.injectJarName) {
                    // [对Transform进行变换] 改变其中 ScanSetting.GENERATE_TO_CLASS_FILE_NAME 对应字节码

                } else {
                    // 直接对Jar进行原地复制 进入output中 (jar 转化为其中的一部分)
                }
            }*/
            copyClassFilesToJarOutputStream(allDirectories, jarOutput)
            copyJarsToJarOutputStream(allJars.get().map { it.asFile }, jarOutput)
        }

        /*var flag = false
        if (jar.exists()) {
            val file = JarFile(jar)
            val enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement() as JarEntry
                val entryName = jarEntry.name
                if (ScanSetting.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                    flag = true
                    // TODO inject logic to this method
                }
            }
            file.close()
        }*/

        /*if (!flag) {
            error("Can not find inject class")
        }*/
    }


    /*将Class文件集合输出到JarOutputStream中，注意他一般只是作为整个流操作的一部分
    * 因此不存在关闭等相关环节，此外这里也没进行异常处理 注意Entry的格式为: com/android/xxx 之类的 相对路径*/
    @Throws(Exception::class)
    fun copyClassFilesToJarOutputStream(
        dirs: ListProperty<Directory>,
        outputStream: JarOutputStream
    ) {
        /*
        * C:\yeahka\ArouterKspCompiler\app\build\tmp\kotlin-classes\debug
            Class entry name C:/yeahka/ArouterKspCompiler/app/build/tmp/kotlin-classes/debug/cn/jailedbird/arouter/ksp/App.class
            Class entry name C:/yeahka/ArouterKspCompiler/app/build/tmp/kotlin-classes/debug/cn/jailedbird/arouter/ksp/JsonServiceImpl.class
        * We need: cn/jailedbird/arouter/ksp/JsonServiceImpl.class
        * */
        dirs.get().forEach { directory ->
            val rootPath =
                directory.asFile.absolutePath.replace(File.separatorChar, '/') + '/'
            // println("Directory is $rootPath")
            directory.asFile.walk().forEach { classFile ->
                if (classFile.isFile) {
                    val absPath = classFile.path.replace(File.separatorChar, '/')
                    val entryName = absPath.substringAfter(rootPath)
                    // println("My entry name $entryName")
                    // 创建Jar条目并设置其名称
                    val entry = JarEntry(entryName)
                    // 将条目添加到目标Jar文件中
                    outputStream.putNextEntry(entry)
                    // 复制Class文件内容到目标Jar文件中
                    classFile.inputStream().use { input ->
                        input.copyTo(outputStream)
                    }
                    // 关闭当前条目
                    outputStream.closeEntry()
                }
            }
        }
    }

    /*文件处理细节
     *Jar file is C:\yeahka\ArouterKspCompiler\app\build\intermediates\compile_and_runtime_not_namespaced_r_class_jar\debug\R.jar
        Jar entry is com/alibaba/android/arouter/R$attr.class
        Jar entry is com/alibaba/android/arouter/R$color.class
        Jar entry is com/alibaba/android/arouter/R$dimen.class
        Jar entry is com/alibaba/android/arouter/R$drawable.class
     * */
    @Throws(Exception::class)
    fun copyJarsToJarOutputStream(jars: List<File>, outputStream: JarOutputStream) {
        for (jarFile in jars) {
            // println("Jar file is $jarFile")
            // 打开源Jar文件
            val sourceJar = JarFile(jarFile)
            // 遍历源Jar文件的条目
            val entries = sourceJar.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                try {
                    if (entry.isDirectory) { // Exclude directory
                        continue
                    }
                    // println("\tJar entry is ${entry.name}")
                    if (entry.name != ScanSetting.GENERATE_TO_CLASS_FILE_NAME) {
                        // 创建目标Jar文件的条目
                        val destinationEntry = JarEntry(entry.name)
                        // 将条目添加到目标Jar文件中
                        outputStream.putNextEntry(destinationEntry)
                        // 复制源Jar文件中的内容到目标Jar文件中
                        val inputStream = sourceJar.getInputStream(entry)
                        IOUtils.copy(inputStream, outputStream)
                        inputStream.close()
                        // 关闭当前条目
                        outputStream.closeEntry()
                    } else {
                        // println("Inject byte code")
                        // 注入代码
                        val inputStream = sourceJar.getInputStream(entry)
                        val registerCodeGenerator =
                            RegisterCodeGenerator(RegisterTransform.registerList)
                        val resultByteArray = registerCodeGenerator.referHackWhenInit(inputStream)
                        inputStream.close()
                        // 创建目标Jar文件的条目
                        val destinationEntry = JarEntry(entry.name)
                        // 将条目添加到目标Jar文件中
                        outputStream.putNextEntry(destinationEntry)
                        IOUtils.copy(ByteArrayInputStream(resultByteArray), outputStream)
                        outputStream.closeEntry()
                    }
                } catch (e: Exception) {
                    // println("Merge jar error entry:${entry.name}, error is $e ")
                }

            }
            // 关闭源Jar文件
            sourceJar.close()
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