package cn.jailedbird.arouter_gradle_plugin.utils

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Scan all class in the package: com/alibaba/android/arouter/
 * find out all routers, interceptors and providers
 */
object ScanUtil {

    /**
     * scan jar file
     * @param jarFile All jar files that are compiled into apk
     * @param destFile dest file after this transform
     */
    fun scanJar(jarFile: File/*, destFile: File*/) {
        if (jarFile.exists()) {
            val file = JarFile(jarFile)
            val enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement() as JarEntry
                val entryName = jarEntry.name
                if (entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)) {
                    val inputStream = file.getInputStream(jarEntry)
                    scanClass(inputStream)
                    inputStream.close()
                } else if (ScanSetting.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                    // mark this jar file contains LogisticsCenter.class
                    // After the scan is complete, we will generate register code into this file
                    RegisterTransform.injectJarName = jarFile.absolutePath
                }
            }
            file.close()
        }
    }

    fun shouldProcessPreDexJar(path: String): Boolean {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    fun shouldProcessClass(entryName: String?): Boolean {
        return entryName != null && entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)
    }

    /**
     * scan class file
     * @param class file
     */
    fun scanClass(file: File) {
        scanClass(FileInputStream(file))
    }

    private fun scanClass(inputStream: InputStream) {
        val cr = ClassReader(inputStream)
        val cw = ClassWriter(cr, 0)
        val cv = ScanClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    class ScanClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {
        override fun visit(
            version: Int,
            access: Int,
            name: String?,
            signature: String?,
            superName: String?,
            interfaces: Array<String>?
        ) {
            super.visit(version, access, name, signature, superName, interfaces)
            RegisterTransform.registerList.forEach { ext ->
                interfaces?.forEach { itName ->
                    if (itName == ext.interfaceName) {
                        // fix repeated inject init code when Multi-channel packaging
                        if (name != null && !ext.classList.contains(name)) {
                            ext.classList.add(name)
                        }
                    }
                }
            }
        }
    }
}