package cn.jailedbird.arouter_gradle_plugin

import cn.jailedbird.arouter_gradle_plugin.utils.ScanSetting
import org.objectweb.asm.*
import java.io.InputStream

/**
 * generate register code into LogisticsCenter.class
 */
class RegisterCodeGenerator(private val extensions: List<ScanSetting>) {

    companion object {
        /*fun insertInitCodeTo(registerSetting: ScanSetting) {
            if (registerSetting != null && registerSetting.classList.isNotEmpty()) {
                val processor = RegisterCodeGenerator(registerSetting)
                val file = RegisterTransform.fileContainsInitClass
                if (file.name.endsWith(".jar")) {
                    processor.insertInitCodeIntoJarFile(file)
                }
            }
        }*/
    }

    /**
     * generate code into jar file
     * @param jarFile the jar file which contains LogisticsCenter.class
     * @return the modified jar file
     */
    /*private fun insertInitCodeIntoJarFile(jarFile: File): File {
        if (jarFile.exists()) {
            val optJar = File(jarFile.parent, jarFile.name + ".opt")
            if (optJar.exists()) {
                optJar.delete()
            }
            val file = JarFile(jarFile)
            val enumeration = file.entries()
            val jarOutputStream = JarOutputStream(FileOutputStream(optJar))

            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement() as JarEntry
                val entryName = jarEntry.name
                val zipEntry = ZipEntry(entryName)
                val inputStream = file.getInputStream(jarEntry)
                jarOutputStream.putNextEntry(zipEntry)
                if (ScanSetting.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                    Logger.i("Insert init code to class >> $entryName")
                    val bytes = referHackWhenInit(inputStream)
                    jarOutputStream.write(bytes)
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                inputStream.close()
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            file.close()

            if (jarFile.exists()) {
                jarFile.delete()
            }
            optJar.renameTo(jarFile)
        }
        return jarFile
    }*/

    // refer hack class when object init
    fun referHackWhenInit(inputStream: InputStream): ByteArray {
        val cr = ClassReader(inputStream)
        val cw = ClassWriter(cr, 0)
        val cv = MyClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    private inner class MyClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String?,
            interfaces: Array<String>?
        ) {
            super.visit(version, access, name, signature, superName, interfaces)
        }

        override fun visitMethod(
            access: Int,
            name: String,
            desc: String,
            signature: String?,
            exceptions: Array<String>?
        ): MethodVisitor {
            var mv = super.visitMethod(access, name, desc, signature, exceptions)
            // generate code into this method
            if (name == ScanSetting.GENERATE_TO_METHOD_NAME) {
                mv = RouteMethodVisitor(Opcodes.ASM5, mv)
            }
            return mv
        }
    }

    private inner class RouteMethodVisitor(api: Int, mv: MethodVisitor) : MethodVisitor(api, mv) {

        override fun visitInsn(opcode: Int) {
            // generate code before return
            if (opcode in Opcodes.IRETURN..Opcodes.RETURN) {
                extensions.forEach { scanSetting ->
                    scanSetting.classList.forEach { name ->
                        val className = name.replace("/", ".")
                        mv.visitLdcInsn(className)// 类名
                        // generate invoke register method into LogisticsCenter.loadRouterMap()
                        mv.visitMethodInsn(
                            Opcodes.INVOKESTATIC,
                            ScanSetting.GENERATE_TO_CLASS_NAME,
                            ScanSetting.REGISTER_METHOD_NAME,
                            "(Ljava/lang/String;)V",
                            false
                        )
                    }
                }
            }
            super.visitInsn(opcode)
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack + 4, maxLocals)
        }
    }
}
