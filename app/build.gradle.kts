plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.mit.timie"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.mit.timie"
        minSdk = 24
        targetSdk = 36
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.recyclerview)
    
    testImplementation(libs.junit)
    testImplementation(libs.junit.quickcheck.core)
    testImplementation(libs.junit.quickcheck.generators)
    
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}