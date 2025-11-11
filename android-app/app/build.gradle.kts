import java.util.Properties
import java.io.FileInputStream

val configProps = Properties()
val configPropsFile = rootProject.file("config.properties")
if (configPropsFile.exists()) {
    configProps.load(FileInputStream(configPropsFile))
}

val serverAddress: String = configProps.getProperty("SERVER_ADDRESS", "REPLACE_ME")

plugins {
    id("com.android.application")
    id("androidx.room")
}

android {
    namespace = "com.vickydegres.lyricsparser"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vickydegres.lyricsparser"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SERVER_ADDRESS", "\"$serverAddress\"")
    }

    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    implementation ("androidx.core:core-ktx:1.13.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("commons-net:commons-net:3.6")



    // Room
    val roomVersion = "2.6.0"

    implementation ("androidx.room:room-runtime:$roomVersion")
    implementation ("androidx.room:room-rxjava3:$roomVersion")
    annotationProcessor ("androidx.room:room-compiler:$roomVersion")

    // RxJava

    implementation ("io.reactivex.rxjava3:rxjava:3.1.6")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.2")

    // Volley

    implementation ("com.android.volley:volley:1.2.1")
}