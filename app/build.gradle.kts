plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.heapiphy101.waqt"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.heapiphy101.waqt"
        minSdk = 26
        targetSdk = 33
        versionCode = 14
        versionName = "5.5"

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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    val lottieVersion = "6.0.1"
    implementation("com.airbnb.android:lottie:$lottieVersion")
    
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.13.3")

    implementation ("com.google.android.gms:play-services-location:21.0.1")

    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation ("com.github.angads25:toggle:1.1.0")

    implementation ("com.google.android.play:app-update-ktx:2.1.0")
    implementation("com.google.android.play:app-update:2.1.0")

    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.5.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

}