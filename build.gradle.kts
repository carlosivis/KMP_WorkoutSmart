plugins {
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.detekt) apply true
}
allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        toolVersion = detekt.toolVersion

        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))

        buildUponDefaultConfig = true
    }
}
