import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*


plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktechMavenPublish)
}

android {
    namespace = "com.abdulrahman_b.hijridatepicker"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")


        aarMetadata {
            minCompileSdk = 26
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.hijrahdatetime)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.coreIcons)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.withType<Test> {
    useJUnitPlatform()
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

    configure(
        AndroidSingleVariantLibrary(
            variant = "release",
            sourcesJar = true,
            publishJavadocJar = true,
        )
    )
    pom { configurePom(this) }

    signAllPublications()
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