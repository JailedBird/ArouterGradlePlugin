# ArouterGradlePlugin

[![GitHub stars](https://img.shields.io/github/stars/JailedBird/ArouterGradlePlugin.svg)](https://github.com/JailedBird/ArouterGradlePlugin/stargazers) [![GitHub forks](https://img.shields.io/github/forks/JailedBird/ArouterGradlePlugin.svg)](https://github.com/JailedBird/ArouterGradlePlugin/network/members) [![GitHub issues](https://img.shields.io/github/issues/JailedBird/ArouterGradlePlugin.svg)](https://github.com/JailedBird/ArouterGradlePlugin/issues) [![GitHub license](https://img.shields.io/github/license/JailedBird/ArouterGradlePlugin.svg)](https://github.com/JailedBird/ArouterGradlePlugin/blob/master/LICENSE)

## 简介

本插件可实现AGP7.4+和AGP8下[ARouter](https://github.com/alibaba/ARouter)框架自动化插桩，使用方法和`com.alibaba:arouter-register` 完全一致，无缝替换；



## 导入方法

插件发布在 [ArouterPlugin](https://plugins.gradle.org/plugin/io.github.JailedBird.ARouterPlugin) ，点开即可查阅最全面的插件导入方式；

**Koltin**

Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):

```kotlin
plugins {
    id("io.github.JailedBird.ARouterPlugin") version "1.0.1"
}
```

Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):

```kotlin
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.github.JailedBird:arouter-gradle-plugin:1.0.1")
    }
}

apply(plugin = "io.github.JailedBird.ARouterPlugin")
```

**Grovvy**

Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):

```groovy
plugins {
  id "io.github.JailedBird.ARouterPlugin" version "1.0.1"
}
```

Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):

```groovy
buildscript {
  repositories {
    gradlePluginPortal()
  }
  dependencies {
    classpath "io.github.JailedBird:arouter-gradle-plugin:1.0.1"
  }
}

apply plugin: "io.github.JailedBird.ARouterPlugin"
```



## 插桩代码

Gradle遍历核心获取核心类，然后在loadRouterMap函数中插桩

```
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
```

插桩后字节码如下：

```
.method public static loadRouterMap()V
    .registers 1

    .line 63
    const/4 v0, 0x0

    sput-boolean v0, Lcom/alibaba/android/arouter/core/LogisticsCenter;->registerByPlugin:Z

    .line 68
    const-string v0, "com.alibaba.android.arouter.routes.ARouter$$Root$$app"

    invoke-static {v0}, Lcom/alibaba/android/arouter/core/LogisticsCenter;->register(Ljava/lang/String;)V

    const-string v0, "com.alibaba.android.arouter.routes.ARouter$$Root$$arouterapi"

    invoke-static {v0}, Lcom/alibaba/android/arouter/core/LogisticsCenter;->register(Ljava/lang/String;)V

    const-string v0, "com.alibaba.android.arouter.routes.ARouter$$Providers$$app"

    invoke-static {v0}, Lcom/alibaba/android/arouter/core/LogisticsCenter;->register(Ljava/lang/String;)V

    const-string v0, "com.alibaba.android.arouter.routes.ARouter$$Providers$$arouterapi"

    invoke-static {v0}, Lcom/alibaba/android/arouter/core/LogisticsCenter;->register(Ljava/lang/String;)V

    return-void
.end method
```



## 参考文献

- https://docs.gradle.org/

- https://github.com/android/gradle-recipes

- [你的插件想适配Transform Action? 可能还早了点](https://juejin.cn/post/7190196880469393463)

- [Transform API 废弃了，路由插件怎么办？](https://juejin.cn/post/7222091234100330554)

## 最后

相关文档见[issue1](https://github.com/JailedBird/ArouterGradlePlugin/issues/1)，如果对您有帮助，请点亮star支持作者😘
