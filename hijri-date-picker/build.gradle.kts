import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties


plugins {
    alias(libs.plugins.android.kotlinMultiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.vanniktechMavenPublish)
}

kotlin {

    android {
        namespace = "com.abdulrahman_b.hijridatepicker"
        compileSdk = 36
        minSdk = 26
        androidResources.enable = true
        aarMetadata {
            this.minCompileSdk = 26
        }
        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
    ).forEach { target ->
        target.binaries.framework {
            baseName = "HijriDatePicker"
            isStatic = true
            binaryOption("bundleId", "com.abdulrahman_b.hijridatepicker.framework")
        }
    }

    sourceSets {
        applyDefaultHierarchyTemplate()
        commonMain.dependencies {
            implementation(libs.composeMultiplatform.runtime)
            implementation(libs.composeMultiplatform.foundation)
            implementation(libs.composeMultiplatform.material3)
            implementation(libs.composeMultiplatform.ui)
            implementation(libs.composeMultiplatform.ui.graphics)
            implementation(libs.composeMultiplatform.ui.tooling.preview)
            api(libs.composeMultiplatform.components.resources)
            implementation(libs.composeMultiplatform.material.iconsCore)

            implementation(libs.kotlinx.datetime)
            api(libs.hijrahdatetime)

        }
        val jvmCommonMain by creating {
            dependsOn(commonMain.get())
        }
        jvmMain.get().dependsOn(jvmCommonMain)
        androidMain.get().dependsOn(jvmCommonMain)
    }

}

compose {
    resources {
        packageOfResClass = "com.abdulrahman_b.hijridatepicker.resources"
    }
}

dependencies {
    androidRuntimeClasspath(libs.composeMultiplatform.ui.tooling.asProvider())
}

/**
 * Loads properties from the given [path] if the file exists, otherwise returns
 * an empty [Properties] instance.
 *
 * This allows the project to be built by anyone cloning the repository without
 * requiring custom `gradle.properties` files in the project directory or the
 * Gradle user home.
 */
fun loadPropertiesOrEmpty(path: File): Properties = Properties().apply {
    if (path.exists()) {
        path.reader().use { reader ->
            load(reader)
        }
    }
}

/**
 * Project-level Gradle properties used primarily to configure the published POM
 * (name, description, URLs, etc.). When the corresponding `gradle.properties`
 * file does not exist, these values will simply be absent and publishing can
 * either provide defaults or be skipped.
 */
val gradleProperties: Properties = loadPropertiesOrEmpty(
    rootProject.file("gradle.properties")
)

/**
 * Global Gradle user properties, typically located under the Gradle user home
 * directory (e.g. `~/.gradle/gradle.properties`). These are intended to hold
 * private credentials such as repository usernames and passwords.
 *
 * If the file does not exist, the returned [Properties] is empty, which allows
 * normal builds to succeed while disabling publishing that depends on those
 * credentials.
 */
val globalGradleProperties: Properties = loadPropertiesOrEmpty(
    File(gradle.gradleUserHomeDir, "gradle.properties")
)


mavenPublishing {

    coordinates(
        groupId = rootProject.group.toString(),
        artifactId = rootProject.group.toString().substringAfterLast('.'),
        version = rootProject.version.toString()
    )

    configure(KotlinMultiplatform(sourcesJar = true))
    pom { configurePom(this) }

    signAllPublications()
}

publishing {
    repositories {
        maven {
            name = "Reposilite"
            if (rootProject.version.toString().endsWith("SNAPSHOT"))
                url = uri("https://maven.abdulrahman-b.com/snapshots")
            else
                url = uri("https://maven.abdulrahman-b.com/releases")

            credentials {
                username = globalGradleProperties.getProperty("reposilite.username")
                password = globalGradleProperties.getProperty("reposilite.password")
            }
        }
    }
}

private fun configurePom(target: MavenPom) = with(target) {
    name = gradleProperties.getProperty("project.name")
    description = gradleProperties.getProperty("project.description")
    url = gradleProperties.getProperty("project.url")
    licenses {
        license {
            name = gradleProperties.getProperty("project.license.name")
            url = gradleProperties.getProperty("project.license.url")
        }
    }

    developers {
        developer {
            id = gradleProperties.getProperty("developer.id")
            name = gradleProperties.getProperty("developer.name")
            email = gradleProperties.getProperty("developer.email")
        }
    }

    scm {
        connection.set(gradleProperties.getProperty("scm.connection"))
        developerConnection.set(gradleProperties.getProperty("scm.developerConnection"))
        url.set(gradleProperties.getProperty("scm.url"))
    }
}