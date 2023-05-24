# ArouterKspCompiler
KSP annotation processor for Arouter

ArouterKspCompiler插件是对ARouter注解处理器（com.alibaba:arouter-compiler）插件的[KSP（Kotlin Symbol Processing API）](https://kotlinlang.org/docs/ksp-overview.html)版本实现，旨在使用KSP提升kotlin注解的处理速度；为何使用KSP注解处理器以及其优点请参考如下引言：

>  Kotlin Symbol Processing (*KSP*) is an API that you can use to develop lightweight compiler plugins. KSP provides a simplified compiler plugin API that leverages the power of Kotlin while keeping the learning curve at a minimum. Compared to [kapt](https://kotlinlang.org/docs/kapt.html), annotation processors that use KSP can run up to 2 times faster. 



## 接入指南：

### kotlin && ksp配置

1、 导入KSP插件，因KSP插件和Kotlin版本高度相关， 因此双方需要匹配；KSP版本号查询地址 [KSP Release](https://github.com/google/ksp/releases)



**前面提到， KSP版本号需要和kotlin匹配， 理论上KSP版本号越高， BUG越少， 因此我会打出各种kotlin版本的插件， 供大家使用(尽量使用高版本)**

版本发布地址:https://jitpack.io/#JailedBird/ArouterKspCompiler, 版本号可做如下参考:

| kotlin版本号 | 最新ArouterKspCompiler | 建议KSP插件版本号 |
| ------------ | ---------------------- | ----------------- |
| 1.6.10       | 1.6.10-1.0.2           | 1.6.10-1.0.4      |
| 1.7.20       | 1.7.20-1.0.2           | 1.7.20-1.0.8      |
| 1.7.22       | 1.7.22-1.0.2           | 1.7.22-1.0.8      |
| 1.8.0        | 1.8.0-1.0.2            | 1.8.0-1.0.9       |
| 1.8.10       | 1.8.10-1.0.2           | 1.8.10-1.0.9      |
| 1.8.20       | 1.8.20-1.0.2           | 1.8.20-1.0.10     |



**ArouterKspCompiler中master分支默认使用kotlin 1.6.10、ksp1.6.10-1.0.4**， 下文中kotlin版本号暂时使用1.6.10替代， 实际使用请按照项目kotlin版本、上述表格灵活替换；

根目录下配置ksp的版本号

```kotlin
buildscript {
    ext.kotlin_version = '1.6.10'
}

plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.6.10' apply false
    id 'com.google.devtools.ksp' version '1.6.10-1.0.4' apply false
}
```

在需要使用Arouter的模块中添加插件和参数配置：

老方法配置kapt的方式如下：

```plain
apply plugin: 'kotlin-kapt'

kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", project.getName())
    }
}
```

使用ArouterKspCompiler之后：

```kotlin
plugins {
    id 'kotlin-android'
    id 'com.google.devtools.ksp'
}
// or--> apply plugin: 'kotlin-android'
// or--> apply plugin: 'com.google.devtools.ksp'
ksp {
    arg("AROUTER_MODULE_NAME", project.getName())
}
```

其他参数配置类似即可



**注意：纯Java模块（没有'kotlin-android'插件）建议直接使用annotationProcess导入Arouter原生处理器（com.alibaba:arouter-compiler）即可； 原因如下：**

- 对纯Java项目而言使用Ksp没有很大的优势，相反KSP可能花费更多的时间去兼容、解析Java；
- Ksp虽然兼容Java， 但是兼容性不一定完美； 如可空，可变等细节上的差异，对某些java信息的获取不一定精准；
- KSP不断在迭代， 不可避免会存在潜在的问题，因此在java模块中直接Arouter注解处理器更加安全；



**但是只要使用KSP的话, 就需要添加'kotlin-android'插件, 否则无法触发代码生成**



最后如果需要模块内的生成的KSP代码可以被IDE识别， 请在模块的build.gradle中任意一行进行如下的配置：

```kotlin
android.sourceSets.all { it.java.srcDir("build/generated/ksp/${it.name}/kotlin/") }
```

注：ksp生成的代码默认会被编译到app壳中，但是默认对IDE不可见， Arouter中生成的代码也不需要对开发者可见，这句话不是必要的；



### ArouterKspCompiler接入

项目添加jitpack仓库

```kotlin
maven { url 'https://jitpack.io' }
```

Arouter模块中导入Ksp插件：

```kotlin
ksp 'com.github.JailedBird:ArouterKspCompiler:1.6.10-1.0.0'
```

此时编译项目，可以在模块build文件找到对应生成的文件， 对此大家可以通过 生成文件源码、Json路由表文件（需开启Doc配置生成）等对比去检验插件功能是否正常；

### 增量编译及其日志

建议在 `gradle.properties` 开启如下三个配置

```kotlin
# KSP Incremental processing
# https://kotlinlang.org/docs/ksp-incremental.html#program-elements
ksp.incremental=true
ksp.incremental.log=true
# track classpath
ksp.incremental.intermodule=true
```

三个开关分别是：

- 增量编译， 默认开启 
- 增量编译的相关日志, 保存文件依赖和变更记录等信息（路径：build/kspcache） 
- 我不是很熟，但是建议开启



### 注意事项

1、 纯Java模块直接使用annotationProcessor导入Arouter原生注解处理器即可， 不要改为ksp导入，避免潜在的bug

2、 如果从纯Java项目迁移为kotlin、Java混编； 就必须使用KSP（或者KAPT）了，不要忘记加入`kotlin-android` 插件， 缺少貌似会导致不生成代码

3、 ArouterKspCompiler内部依赖'com.alibaba:arouter-annotation:1.0.6'， 按照Arouter目前更新状态不存在变化的可能

4、 关于混淆等其他任何配置， 直接按照Arouter的来就可以了

5、 本人会长期维护此代码仓库：[ArouterKspCompiler](https://github.com/JailedBird/ArouterKspCompiler) ， 如果有问题可以提issue、加微信 JailedBird；如果对您有帮助， 请一键三联支持一波🤣



### 参考文献

- https://kotlinlang.org/docs/ksp-overview.html#symbolprocessorprovider-the-entry-point



