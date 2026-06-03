plugins {
    // AGP 9 has built-in Kotlin support — do NOT also apply
    // org.jetbrains.kotlin.android or it double-registers the `kotlin` extension.
    id("com.android.library")
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

    // AGP 8+/9 require explicitly declaring which variant is published so the
    // `components["release"]` software component exists for maven-publish below.
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // ironSource / Unity LevelPlay — the unified mediation SDK. It BUNDLES Unity
    // Ads internally, so the former standalone `com.unity3d.ads:unity-ads` MUST
    // NOT be added alongside it (duplicate com.unity3d classes). One rung
    // (AdNetwork.IRONSOURCE) serves both ironSource + Unity demand via LevelPlay.
    implementation("com.unity3d.ads-mediation:mediation-sdk:9.4.0")
    // LevelPlay GAID / App Set ID requirements.
    implementation("com.google.android.gms:play-services-appset:16.0.2")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.google.android.gms:play-services-basement:18.3.0")

    implementation("com.google.android.gms:play-services-ads:23.3.0")
    implementation("com.facebook.android:audience-network-sdk:6.21.0")


    // App-level foreground/background detection for App Open ads
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")

    // Google UMP (GDPR Consent) SDK
    implementation("com.google.android.ump:user-messaging-platform:3.1.0")

    // Firebase BoM — version ek jagya thi manage thay
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("androidx.core:core-ktx:1.15.0")

    // Unit tests. org.json is only a stub in android.jar, so pull the real
    // implementation onto the JVM test classpath to exercise RemoteConfigParser.
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20231013")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.demo.mydemo"
                artifactId = "ads-libs"
                version = "1.3"
            }
        }
    }
}
