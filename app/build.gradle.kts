plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
<<<<<<< HEAD
    // Tambahkan plugin kapt jika ingin menggunakan Room (Praktikum 4)
    id("kotlin-kapt")
=======
>>>>>>> 6a9a5b3 (first commit)
}

android {
    namespace = "com.abel.mobilin"
<<<<<<< HEAD
    // Perbaikan: Gunakan API 35 (Stable) karena API 36 masih sangat baru/preview
    compileSdk = 36
=======
    compileSdk {
        version = release(36)
    }
>>>>>>> 6a9a5b3 (first commit)

    defaultConfig {
        applicationId = "com.abel.mobilin"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
<<<<<<< HEAD
        viewBinding = true // Sangat disarankan untuk memudahkan akses UI
=======
        viewBinding = true
>>>>>>> 6a9a5b3 (first commit)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
<<<<<<< HEAD

    // Praktikum 8: Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation(libs.firebase.database)

    // Praktikum 4: Room Database (Tambahan untuk penyimpanan lokal)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Praktikum 9: Cloudinary untuk Unggah Gambar
    implementation("com.cloudinary:cloudinary-android:2.5.0")

    // Praktikum 7: Retrofit (Perbaikan spasi pada nama library)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Diperbaiki dari "squ areup"

    // Praktikum 5: Glide untuk Menampilkan Gambar dari URL
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

=======
    implementation("com.google.firebase:firebase-auth:22.3.0")
>>>>>>> 6a9a5b3 (first commit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}