import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost
import java.util.*


plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktechMavenPublish)
}

android {
    namespace = "com.abdulrahman_b.hijridatepicker"
    compileSdk = 35

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
    kotlinOptions {
        jvmTarget = "11"
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

val gradleProperties = Properties().apply {
    load(file("../gradle.properties").reader())
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

    configure(
        AndroidSingleVariantLibrary(
            variant = "release",
            sourcesJar = true,
            publishJavadocJar = true,
        )
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