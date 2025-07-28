plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// 版本配置
val versionMajor = 0
val versionMinor = 12
val versionPatch = 0
val versionClassifier = "beta" // or null
val isSnapshot = true // set to false when publishing new releases
val minimumSdkVersion = 24
val targetSdkVersion = 34

fun generateVersionCode(): Int {
    return minimumSdkVersion * 10000000 + versionMajor * 10000 + versionMinor * 100 + versionPatch
}

fun generateVersionName(): String {
    var versionName = "$versionMajor.$versionMinor.$versionPatch"
    if (versionClassifier != null) {
        versionName += "-$versionClassifier"
    }
    if (isSnapshot) {
        versionName += "-SNAPSHOT"
    }
    return versionName
}

android {
    namespace = "io.github.trojan_gfw.igniter"
    compileSdk = targetSdkVersion
    ndkVersion = "23.1.7779620"

    applicationVariants.all {
        resValue("string", "versionName", versionName!!)
    }

    defaultConfig {
        applicationId = "io.github.trojan_gfw.igniter"
        minSdk = minimumSdkVersion
        targetSdk = targetSdkVersion
        versionCode = generateVersionCode()
        versionName = generateVersionName()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }

        externalNativeBuild {
            cmake {
                arguments += "-DANDROID_CPP_FEATURES=rtti exceptions"
            }
        }

    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true  // 确保启用 BuildConfig 生成
        aidl = true  // 明确启用AIDL支持
    }

    sourceSets {
        getByName("main") {
            aidl.srcDirs("src/main/aidl")
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        jniLibs {
            // During installation, the installer decompresses the libraries, and the linker loads
            // the decompressed libraries at runtime; in this case, the APK would be smaller, but
            // installation time might be slightly longer.
            // We optimize for size to make users happy
            useLegacyPackaging = true
            keepDebugSymbols += listOf(
                "*/armeabi-v7a/libgojni.so",
                "*/arm64-v8a/libgojni.so",
                "*/x86/libgojni.so",
                "*/x86_64/libgojni.so"
            )
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }
}

dependencies {
    implementation("com.github.stealthcopter:AndroidNetworkTools:0.4.5.3")
    implementation("com.google.android.material:material:1.6.0-alpha01")
    implementation("androidx.activity:activity:1.4.0")
    implementation("androidx.fragment:fragment:1.4.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation("androidx.core:core:1.8.0-alpha02")
    implementation("androidx.preference:preference:1.2.0-rc01")
    implementation("com.google.code.gson:gson:2.8.9")

    // CameraX core library using camera2 implementation
    implementation("androidx.camera:camera-camera2:1.1.0-alpha12")
    // CameraX Lifecycle Library
    implementation("androidx.camera:camera-lifecycle:1.1.0-alpha12")
    // CameraX View class
    implementation("androidx.camera:camera-view:1.0.0-alpha32")
    implementation("com.google.mlkit:barcode-scanning:17.0.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    // 注意：golibs.aar 需要手动处理，因为它是本地AAR文件
    implementation(files("src/libs/golibs.aar"))
}