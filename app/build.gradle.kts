import org.gradle.kotlin.dsl.implementation

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.myprojecttreker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myprojecttreker"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

    dependencies {
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.work:work-runtime-ktx:2.9.0")
        implementation("androidx.compose.material:material-icons-extended")
        implementation(platform("androidx.compose:compose-bom:2024.06.00"))
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation("androidx.activity:activity-compose:1.9.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
        implementation("androidx.navigation:navigation-compose:2.7.7")
        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.androidx.work.runtime.ktx)
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
        implementation("androidx.activity:activity-compose:1.9.0")
        implementation("androidx.room:room-runtime:2.7.0")
        implementation("androidx.room:room-ktx:2.7.0")
        kapt("androidx.room:room-compiler:2.7.0")
        implementation(platform("androidx.compose:compose-bom:2024.06.00"))
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.material3:material3")
        implementation("androidx.compose.ui:ui-tooling-preview")
    }

