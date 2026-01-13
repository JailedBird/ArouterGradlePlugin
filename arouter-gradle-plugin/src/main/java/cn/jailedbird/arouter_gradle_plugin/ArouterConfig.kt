package cn.jailedbird.arouter_gradle_plugin

open class ARouterConfig {
    /** 仅在 Debug 构建时禁用 Transform（Release 仍会执行） */
    var disableTransformWhenDebugBuild: Boolean = false

    /** 完全禁用 Transform（所有构建类型都不执行） */
    var disableTransform: Boolean = false
}