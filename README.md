# ArouterGradlePlugin

[![GitHub stars](https://img.shields.io/github/stars/JailedBird/ArouterGradlePlugin.svg)](https://github.com/JailedBird/ArouterGradlePlugin/stargazers) [![GitHub forks](https://img.shields.io/github/forks/JailedBird/ArouterGradlePlugin.svg)](https://github.com/JailedBird/ArouterGradlePlugin/network/members) [![GitHub issues](https://img.shields.io/github/issues/JailedBird/ArouterGradlePlugin.svg)](https://github.com/JailedBird/ArouterGradlePlugin/issues) [![GitHub license](https://img.shields.io/github/license/JailedBird/ArouterGradlePlugin.svg)](https://github.com/JailedBird/ArouterGradlePlugin/blob/master/LICENSE)

## 简介

本插件可实现AGP7.4+下[ARouter](https://github.com/alibaba/ARouter)
框架自动注册，使用方法和`com.alibaba:arouter-register` 完全一致；

## 导入方法

插件发布地址[ArouterPlugin](https://plugins.gradle.org/plugin/io.github.JailedBird.ARouterPlugin)

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




## 参考文献

- https://docs.gradle.org/

- https://github.com/android/gradle-recipes

- [你的插件想适配Transform Action? 可能还早了点](https://juejin.cn/post/7190196880469393463)

- [Transform API 废弃了，路由插件怎么办？](https://juejin.cn/post/7222091234100330554)

## 最后

后续我会写对应的文档和实现方式，欢迎star😘
