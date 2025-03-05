plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // ✅ Apply Google Services Plugin
}

android {
    namespace = "com.example.encryptedmessagingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.encryptedmessagingapp"
        minSdk = 24
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // ✅ Firebase Dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.1.0")) // Ensures version consistency
    implementation("com.google.firebase:firebase-auth") // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore") // Firebase Firestore (for chat)
    implementation("com.google.firebase:firebase-messaging")

    // Testing Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// ✅ Apply Google Services Plugin at the bottom
apply(plugin = "com.google.gms.google-services")
