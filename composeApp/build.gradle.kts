import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.build.config)
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
    ?: error("WEB_CLIENT_ID not found in local.properties")

val baseUrl: String = localProperties.getProperty("BASE_URL")
    ?: error("BASE_URL not found in local.properties")


kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
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
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget {
        @Suppress("OPT_IN_USAGE")
        unitTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
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
            implementation(libs.koin.view.model)
            implementation(libs.koin.compose)

            implementation(libs.decompose)
            implementation(libs.decompose.compose)
            implementation(libs.coil.compose)

            implementation(libs.peekaboo.image.picker)
            implementation(libs.peekaboo.ui)

            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.multiplatform.settings)

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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

tasks.register("testClasses") {
    println("This is a dummy testClasses task")
}
