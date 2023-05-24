package cn.jailedbird.arouter_gradle_plugin.utils

import org.objectweb.asm.*
import java.io.InputStream

/**
 * generate register code into LogisticsCenter.class
 */
object InjectUtils {
    // refer hack class when object init
    fun referHackWhenInit(inputStream: InputStream, targetList: List<ScanSetting>): ByteArray {
        val cr = ClassReader(inputStream)
        val cw = ClassWriter(cr, 0)
        val cv = InjectClassVisitor(Opcodes.ASM5, cw, targetList)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    private class InjectClassVisitor(
        api: Int,
        cv: ClassVisitor,
        val targetList: List<ScanSetting>? = null
    ) : ClassVisitor(api, cv) {

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
                mv = RouteMethodVisitor(Opcodes.ASM5, mv, targetList)
            }
            return mv
        }
    }

    private class RouteMethodVisitor(
        api: Int,
        mv: MethodVisitor,
        val targetList: List<ScanSetting>? = null
    ) : MethodVisitor(api, mv) {

        override fun visitInsn(opcode: Int) {
            // generate code before return
            if (opcode in Opcodes.IRETURN..Opcodes.RETURN) {
                targetList?.forEach { scanSetting ->
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
