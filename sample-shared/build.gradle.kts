@file:OptIn(ExperimentalSwiftExportDsl::class)

import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.kotlinMultiplatform.library)
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    android {
        namespace = "com.abdulrahman.hijridatepacker.sample"
        compileSdk = 36
        minSdk = 26

    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "sampleKit"
    val xcFramework = XCFramework("HijriDatePickerSample")
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = xcfName
            xcFramework.add(this)
        }
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain.dependencies {
            implementation(libs.composeMultiplatform.runtime)
            implementation(libs.composeMultiplatform.foundation)
            implementation(libs.composeMultiplatform.material3)
            implementation(libs.composeMultiplatform.ui)
            implementation(libs.composeMultiplatform.ui.graphics)
            implementation(libs.composeMultiplatform.ui.tooling.preview)
            implementation(libs.composeMultiplatform.components.resources)
            implementation(libs.composeMultiplatform.material.iconsCore)

            implementation(libs.kotlinx.datetime)
            implementation(libs.hijrahdatetime)
            implementation(project(":hijri-date-picker"))

        }
    }

}

