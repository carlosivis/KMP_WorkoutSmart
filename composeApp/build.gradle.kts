import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.build.config)
    alias(libs.plugins.kotlin.serialization)
    kotlin("native.cocoapods")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    FileInputStream(localPropertiesFile).use { stream ->
        localProperties.load(stream)
    }
}

val webClientId: String = localProperties.getProperty("WEB_CLIENT_ID")
    ?: System.getenv("WEB_CLIENT_ID")
    ?: "CI_PLACEHOLDER_ID"

val baseUrl: String = localProperties.getProperty("BASE_URL")
    ?: System.getenv("BASE_URL")
    ?: "CI_PLACEHOLDER_URL"


kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            @Suppress("OPT_IN_USAGE")
            jvmTarget.set(JvmTarget.JVM_1_8)
            freeCompilerArgs.addAll(
                listOf(
                    "-Xcontext-receivers",
                    "-Xinline-classes",
                    "-Xexpect-actual-classes"
                )
            )
            progressiveMode.set(true)
        }
    }
    cocoapods {
        version = "1.0"
        summary = "Módulo compartilhado KMP"
        homepage = "https://github.com/carlosivis/workoutsmart"

        ios.deploymentTarget = "13.0"

        framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        pod("FirebaseAuth")
        pod("GoogleSignIn") {
            version = "7.1.0"
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "composeApp"
            isStatic = true
            freeCompilerArgs += listOf("-Xbinary=bundleId=dev.carlosivis.workoutsmart")
        }
    }

    sourceSets {

        commonMain.dependencies {
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.compose.multiplatform.ui)
            implementation(libs.compose.multiplatform.resources)
            implementation(libs.compose.multiplatform.material3)
            implementation(libs.material.icons.extended)
            implementation(libs.compose.multiplatform.ui.tooling.preview)

            implementation(libs.sqldelight.coroutines)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.koin.core)
            implementation(libs.koin.core.view.model)
            implementation(libs.koin.compose)

            implementation(libs.decompose)
            implementation(libs.decompose.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            implementation(libs.peekaboo.image.picker)
            implementation(libs.peekaboo.ui)

            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.multiplatform.settings)

            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.gitlive.firebase.auth)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.ui.tooling)
            implementation(libs.sqldelight.android)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.decompose)
            implementation(libs.decompose.compose)
            implementation(libs.play.services.auth)
            implementation(libs.ktor.client.cio)
            implementation(libs.androidx.credentials.manager)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.googleid.v110)
            implementation(libs.koin.view.model)
            implementation(libs.koin.compose)

        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native)
            implementation(libs.decompose)
            implementation(libs.decompose.compose)
            implementation(libs.ktor.client.darwin)
        }

    }
}

android {
    namespace = "dev.carlosivis.workoutsmart"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
}

buildConfig {
    packageName = "dev.carlosivis.workoutsmart"

    buildConfigField(
        "String",
        "BASE_URL",
        "\"$baseUrl\""
    )

    sourceSets.named("androidMain") {
        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"$webClientId\""
        )
    }
}

sqldelight {
    databases {
        create("WorkoutSmartDatabase") {
            packageName.set("dev.carlosivis.workoutsmart.database")
        }
    }
}
compose.resources {
    packageOfResClass = "dev.carlosivis.workoutsmart.composeResources"
    generateResClass = auto
}
