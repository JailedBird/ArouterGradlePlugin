package cn.jailedbird.arouter_gradle_plugin.utils


object RegisterTransform {
    val registerList = listOf(
        ScanSetting("IRouteRoot"),
        ScanSetting("IInterceptorGroup"),
        ScanSetting("IProviderGroup"),
    )

    // var fileContainsInitClass: File? = null
    var injectJarName:String? = null

}