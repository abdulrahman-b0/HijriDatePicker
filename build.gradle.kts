// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.kotlinMultiplatform.library) apply false
    alias(libs.plugins.android.lint) apply false
}

rootProject.description  = "A modern and stylish Hijri Date Picker for Compose Multiplatform. Inspired by the Material 3 Date Picker."
rootProject.group  = "com.abdulrahman-b.hijridatepicker"
rootProject.version = "2.0.0-alpha01"