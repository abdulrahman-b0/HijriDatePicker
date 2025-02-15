import com.vanniktech.maven.publish.SonatypeHost
import java.util.Properties


plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktechMavenPublish)
}

android {
    namespace = "com.abdulrahman_b.hijridatepicker"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.hijrahdatetime)
    implementation(libs.kotlin.serialization.protobuf)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

val gradleProperties = Properties().apply {
    load(File("gradle.properties").reader())
}
val globalGradleProperties = Properties().apply {
    val userHome = gradle.gradleUserHomeDir
    load(File(userHome, "gradle.properties").reader())
}

mavenPublishing {

    coordinates(
        groupId = rootProject.group.toString(),
        artifactId = rootProject.group.toString().substringAfterLast('.'),
        version = rootProject.version.toString()
    )

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = false)
    pom { configurePom(this) }

    signAllPublications()
}

publishing {
    repositories {
        maven("https://maven.abdulrahman-b.com/releases") {
            name = "Reposilite"
            credentials {
                this.username = globalGradleProperties.getProperty("reposilite.username")
                this.password = globalGradleProperties.getProperty("reposilite.password")
            }
        }
    }
}


private fun Project.configurePom(target: MavenPom) = with(target) {
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
            id = globalGradleProperties.getProperty("developer.id")
            name = globalGradleProperties.getProperty("developer.name")
            email = globalGradleProperties.getProperty("developer.email")
        }
    }

    scm {
        connection.set(gradleProperties.getProperty("scm.connection"))
        developerConnection.set(gradleProperties.getProperty("scm.developerConnection"))
        url.set(gradleProperties.getProperty("scm.url"))
    }
}