import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.geniusdevelop.playmyscreens"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.geniusdevelop.playmyscreens"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            applicationIdSuffix = ".release"
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    productFlavors {
        create("prod") {
            buildConfigField("String", "BASE_URL",
                "\"http://47.204.0.63:82/api/v1\""
            )
            buildConfigField("String", "WS_BASE_URL",
                "\"ws://47.204.0.63:8000/connection/websocket\""
            )
            buildConfigField("String", "WS_SECRET",
                "\"940e1175-d3ec-45ee-adb2-3508272074f3\""
            )
            dimension = "api"
        }
        create("desa") {
            buildConfigField("String", "BASE_URL",
                "\"http://47.204.0.63:82/api/v1\""
            )
            buildConfigField("String", "WS_BASE_URL",
                "\"ws://47.204.0.63:8000/connection/websocket\""
            )
            buildConfigField("String", "WS_SECRET",
                "\"940e1175-d3ec-45ee-adb2-3508272074f3\""
            )
            dimension="api"
        }
        create("devdesa") {
            buildConfigField("String", "BASE_URL",
                "\"http://192.168.1.181/laravel/screen-server/public/api/v1\""
            )
            buildConfigField("String", "WS_BASE_URL",
                "\"ws://192.168.1.181:8000/connection/websocket\""
            )
            buildConfigField("String", "WS_SECRET",
                "\"MEcvUw7o5RpJsgeF9Ay\""
            )
            dimension = "api"
        }
        create("dev") {
            buildConfigField("String", "BASE_URL",
                "\"http://10.0.2.2/screen-server/public/api/v1\""
            )
            buildConfigField("String", "WS_BASE_URL",
                "\"ws://10.0.2.2:8000/connection/websocket\""
            )
            buildConfigField("String", "WS_SECRET",
                "\"MEcvUw7o5RpJsgeF9Ay\""
            )
            dimension = "api"
        }
    }

    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val output = this
            val project = "playmyscreens"
            val flavor = variant.productFlavors[0].name
            val versionName = variant.versionName
            val apkName = "${project}-${flavor}-${versionName}.apk"
            (output as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName = apkName
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    flavorDimensions += listOf("api")
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil.compose)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.material3.android)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.json)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.auth)

    implementation(libs.kotlin.serialization)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlin.jwt)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.centrifuge.java)
    implementation (libs.androidx.lifecycle.extensions)
}