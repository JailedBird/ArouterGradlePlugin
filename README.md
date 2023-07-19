# ArouterGradlePlugin

## 简介

本插件可实现AGP7.4+下[ARouter](https://github.com/alibaba/ARouter)
框架自动注册，使用方法和`com.alibaba:arouter-register` 完全一致；

## 导入方法

插件发布地址[ArouterPlugin](https://plugins.gradle.org/plugin/io.github.JailedBird.ARouterPlugin)

Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):

```kotlin
plugins {
    id("io.github.JailedBird.ARouterPlugin") version "1.0.0-beta02"
}
```

Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):

```kotlin
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("io.github.JailedBird:arouter-gradle-plugin:1.0.0-beta02")
    }
}

apply(plugin = "io.github.JailedBird.ARouterPlugin")
```

## 注意事项

注意本插件为AGP7.4版本， 7.4+尚未尝试（应该是可以执行的、后续会验证并推出专属版本），7.4以下无法使用(使用的API为7.4+限制)；

代码经过测试支持同样支持AGP8+， 请切换[feat-agp8](https://github.com/JailedBird/ArouterGradlePlugin/tree/feat-agp8)分支；

## 参考文献

- https://docs.gradle.org/

- https://github.com/android/gradle-recipes

- [你的插件想适配Transform Action? 可能还早了点](https://juejin.cn/post/7190196880469393463)

- [Transform API 废弃了，路由插件怎么办？](https://juejin.cn/post/7222091234100330554)

## 最后

后续我会写对应的文档和实现方式，欢迎star😘
