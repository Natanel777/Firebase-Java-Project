plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "natanel.android.firebasejavaproject"
    compileSdk = 34

    viewBinding{
        enable = true
    }

    defaultConfig {
        applicationId = "natanel.android.firebasejavaproject"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Glide for image view
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //Firebase Dep (When using the BoM, you don't specify versions in Firebase library dependencies)
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-analytics")

    //Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    //Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")

    //Firebase Storage
    implementation("com.google.firebase:firebase-storage")

    //Firebase Notifications
    implementation("com.google.firebase:firebase-messaging")

}