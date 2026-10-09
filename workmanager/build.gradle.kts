plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ru.mirea.dolgovis.workmanager"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "ru.mirea.dolgovis.workmanager"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation("androidx.work:work-runtime:2.10.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}

android {
    buildFeatures { viewBinding = true }
}