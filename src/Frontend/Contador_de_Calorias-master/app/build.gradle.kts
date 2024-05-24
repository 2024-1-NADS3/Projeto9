plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "app.android.contadordecalorias"
    compileSdk = 34

    defaultConfig {
        applicationId = "app.android.contadordecalorias"
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

    buildFeatures {
        viewBinding = true
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("com.github.blackfizz:eazegraph:1.2.5l@aar")
    implementation("com.nineoldandroids:library:2.4.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}