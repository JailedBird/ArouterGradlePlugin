package cn.jailedbird.arouter_gradle_plugin.utils

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.jar.JarFile

/**
 * Scan all class in the package: com/alibaba/android/arouter/
 * find out all routers, interceptors and providers
 */
@Suppress("SpellCheckingInspection")
object ScanUtils {

    fun scanJar(jarFile: File, targetList: List<ScanSetting>) {
        if (jarFile.exists()) {
            val file = JarFile(jarFile)
            val enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                if (jarEntry.name.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)) {
                    file.getInputStream(jarEntry).use { inputStream ->
                        scanClass(inputStream, targetList, false)
                    }
                }
            }
            file.close()
        }
    }


    @Suppress("unused")
    /**
     * Exclude scan jar, you can custmize yourself
     * */
    fun shouldProcessPreDexJar(path: String): Boolean {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    fun shouldProcessClass(entryName: String): Boolean {
        return entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)
    }

    @Suppress("unused")
    fun scanClass(file: File, targetList: List<ScanSetting>, autoClose: Boolean = true) {
        scanClass(FileInputStream(file), targetList, autoClose)
    }

    fun scanClass(
        inputStream: InputStream, targetList: List<ScanSetting>, autoClose: Boolean = true
    ) {
        val cr = ClassReader(inputStream)
        val cw = ClassWriter(cr, 0)
        val cv = ScanClassVisitor(Opcodes.ASM7, cw, targetList)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        if (autoClose) {
            inputStream.close()
        }
    }

    class ScanClassVisitor(
        api: Int, cv: ClassVisitor, private val targetRegisterList: List<ScanSetting>
    ) : ClassVisitor(api, cv) {
        override fun visit(
            version: Int,
            access: Int,
            name: String?,
            signature: String?,
            superName: String?,
            interfaces: Array<String>?
        ) {
            super.visit(version, access, name, signature, superName, interfaces)
            targetRegisterList.forEach { ext ->
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