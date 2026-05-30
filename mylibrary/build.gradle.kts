plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.ads.adslib"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    implementation("com.unity3d.ads:unity-ads:4.7.0")
    implementation("com.google.android.gms:play-services-ads:23.3.0")
    implementation("com.facebook.android:audience-network-sdk:6.17.0")

    // App-level foreground/background detection for App Open ads
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")

    // Google UMP (GDPR Consent) SDK
    implementation("com.google.android.ump:user-messaging-platform:3.1.0")

    // Firebase BoM — version ek jagya thi manage thay
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("androidx.core:core-ktx:1.15.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.demo.mydemo"
                artifactId = "ads-libs"
                version = "1.2"
            }
        }
    }
}
