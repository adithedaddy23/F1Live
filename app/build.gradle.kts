plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    id("com.android.application")
    id("com.google.gms.google-services")
//    id("com.google.devtools.ksp")



}

android {
    namespace = "com.example.f1live"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.f1live"
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
        compose = true
    }


}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation("com.github.Kyant0.AndroidLiquidGlass:backdrop:1.0.0-alpha11")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    //bottom navigation bar
    implementation("androidx.navigation:navigation-compose:2.9.5")
// latest stable

    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("dev.chrisbanes.haze:haze:1.6.10")
    implementation("dev.chrisbanes.haze:haze-materials:1.6.10")
    implementation("io.github.kyant0:backdrop:1.0.0")
    implementation("io.github.kyant0:capsule:2.1.1")
    implementation("org.jetbrains.compose.material3:material3:1.9.0-alpha04")


    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging-ktx")

    implementation("com.github.anhaki:PickTime-Compose:1.1.4")
    implementation("com.github.commandiron:WheelPickerCompose:1.1.11")
    implementation("io.github.pseudoankit:coachmark:3.0.1")

//    def nav_version = ""

    implementation ("androidx.navigation:navigation-compose:2.8.0-beta02")

    implementation("androidx.glance:glance-appwidget:1.1.0")
    implementation("androidx.glance:glance-material3:1.1.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.google.code.gson:gson:2.10.1")

//    implementation("com.tickaroo.tikxml:annotation:0.8.13")
//    implementation("com.tickaroo.tikxml:core:0.8.13")
//    implementation("com.tickaroo.tikxml:retrofit-converter:0.8.13")
//    ksp("com.tickaroo.tikxml:processor:0.8.13")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0") {
        exclude(module = "stax")
        exclude(module = "stax-api")
        exclude(module = "xpp3")
    }

    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}