plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "eg.iti.mad.climaguard"
    compileSdk = 35

    defaultConfig {
        applicationId = "eg.iti.mad.climaguard"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    val nav_version = "2.8.8"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    //Serialization for NavArgs
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    //Google maps
    implementation("com.google.maps.android:maps-compose:2.14.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    //Scoped API
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")
    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    //okhttp logging interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    //Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    // Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
    //Glide
    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")
    //Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.github.skydoves:landscapist-glide:2.2.9")
    //LiveData & Compose
    val compose_version = "1.0.0"
    implementation ("androidx.compose.runtime:runtime-livedata:$compose_version")
    //dataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    //icons
    implementation("androidx.compose.material:material-icons-extended:1.5.0")
    //WorkManager
    val work_version = "2.10.0"
    implementation("androidx.work:work-runtime-ktx:$work_version")



    // TESTING

//    testImplementation ("junit:junit:4.13.2")
//    androidTestImplementation ("androidx.test.ext:junit:1.2.1")
//    androidTestImplementation ("androidx.test.espresso:espresso-core:3.6.1")
//
//    // Dependencies for local unit tests
//    testImplementation("junit:junit:$junitVersion")
//    testImplementation( "org.hamcrest:hamcrest-all:$hamcrestVersion")
//    testImplementation ("androidx.arch.core:core-testing:$archTestingVersion")
//    testImplementation ("org.robolectric:robolectric:$robolectricVersion")
//
//    // AndroidX Test - JVM testing
//    testImplementation ("androidx.test:core-ktx:$androidXTestCoreVersion")
//    //testImplementation "androidx.test.ext:junit:$androidXTestExtKotlinRunnerVersion"
//
//    // AndroidX Test - Instrumented testing
//    androidTestImplementation ("androidx.test:core:$androidXTestExtKotlinRunnerVersion")
//    androidTestImplementation ("androidx.test.espresso:espresso-core:$espressoVersion")
//
//    //Timber
//    implementation ("com.jakewharton.timber:timber:5.0.1")
//
//    // hamcrest
//    testImplementation ("org.hamcrest:hamcrest:2.2")
//    testImplementation ("org.hamcrest:hamcrest-library:2.2")
//    androidTestImplementation ("org.hamcrest:hamcrest:2.2")
//    androidTestImplementation ("org.hamcrest:hamcrest-library:2.2")
//
//
//    // AndroidX and Robolectric
//    testImplementation ("androidx.test.ext:junit-ktx:$androidXTestExtKotlinRunnerVersion")
//    testImplementation ("androidx.test:core-ktx:$androidXTestCoreVersion")
//    testImplementation ("org.robolectric:robolectric:4.11")
//
//    // InstantTaskExecutorRule
//    testImplementation ("androidx.arch.core:core-testing:2.1.0")
//    androidTestImplementation ("androidx.arch.core:core-testing:2.1.0")
//
//    //kotlinx-coroutines
//    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
//    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
//    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
//
//
//    //MockK
//    testImplementation ("io.mockk:mockk-android:1.13.17")
//    testImplementation ("io.mockk:mockk-agent:1.13.17")

    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    // AndroidX JUnit
    testImplementation ("io.mockk:mockk:1.13.8")
    // AndroidX and Robolectric
    testImplementation ("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation ("androidx.test:core-ktx:1.5.0")
    testImplementation ("org.robolectric:robolectric:4.11.1")
    //kotlinx-coroutines
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")

}