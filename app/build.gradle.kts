plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.facerecognition"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.facerecognition"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.camera:camera-core:1.2.0-rc01")
    implementation("androidx.camera:camera-camera2:1.2.0-rc01")
    implementation("androidx.camera:camera-lifecycle:1.2.0-rc01")
    implementation("androidx.camera:camera-view:1.2.0-rc01")

    implementation("com.android.volley:volley:1.2.1")

    implementation("com.google.mlkit:face-detection:16.1.5")
    implementation("androidx.camera:camera-mlkit-vision:1.2.0-beta02")
}