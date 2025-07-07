plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cf"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cf"
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

    packagingOptions {
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/INDEX.LIST",
                    "META-INF/io.netty.versions.properties",
                    "META-INF/LICENSE",
                    "META-INF/LICENSE.txt",
                    "META-INF/DEPENDENCIES",
                    "META-INF/NOTICE",
                    "META-INF/NOTICE.txt",
                    "META-INF/AL2.0",
                    "META-INF/LGPL2.1"
                )
            )
        }
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
    implementation("io.socket:socket.io-client:2.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.json:json:20211205")
    implementation("com.google.cloud:google-cloud-dialogflow:4.6.0")
    implementation("io.grpc:grpc-okhttp:1.54.0")
    implementation("io.grpc:grpc-protobuf:1.54.0")
    implementation("io.grpc:grpc-stub:1.54.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.20.0")
}