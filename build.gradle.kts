// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
}

rootProject.description  = "A modern and stylish Hijri Date Picker for Android, built with Jetpack Compose and inspired by the Material 3 Date Picker."
rootProject.group  = "com.abdulrahman-b.hijridatepicker"
rootProject.version = "1.0.0"