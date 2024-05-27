plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    kotlin("android")
    jacoco
    kotlin("plugin.serialization")
}

android {
    namespace = "com.example.therapeia"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.therapeia"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.create("jacocoTestReport", JacocoReport::class.java) {
    group = "Reporting"
    description = "Generate Jacoco coverage reports."

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*\$\$serializer.class",
    )

    val developDebug = "developDebug"

    sourceDirectories.setFrom(files(listOf("$projectDir/src/main/java", "$projectDir/src/main/kotlin")))

    classDirectories.setFrom(files(listOf(
        fileTree("$buildDir/intermediates/javac/$developDebug") { exclude(fileFilter) },
        fileTree("$buildDir/tmp/kotlin-classes/$developDebug") { exclude(fileFilter) },
    )))

    executionData.setFrom(fileTree(project.buildDir) {
        include(
            // unit tests
            "jacoco/test${"developDebug".capitalize()}UnitTest.exec",
            // instrumentation tests
            "outputs/code_coverage/${developDebug}AndroidTest/connected/**/*.ec",
        )
    })

    // dependsOn("test${"developDebug".capitalize()}UnitTest")
    // dependsOn("connected${"developDebug".capitalize()}AndroidTest")
}

tasks.withType(Test::class.java) {
    (this.extensions.getByName("jacoco") as JacocoTaskExtension).apply {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }

    systemProperty("org.slf4j.simpleLogger.logFile", "System.out")
    systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
}

val acraEmail =
    project.rootProject
        .file("local.properties")
        .let { if (it.exists()) it.readLines() else emptyList() }
        .firstOrNull { it.startsWith("acra.email") }
        ?.substringAfter("=")
        ?: System.getenv()["ACRA_EMAIL"] ?: ""

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Onboarding screens
    implementation("com.tbuonomo:dotsindicator:5.0")

    // Firebase
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.11.0")

    // Lector PDF
    implementation("com.github.barteksc:android-pdf-viewer:2.8.2")

    // Chat
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("io.coil-kt:coil:1.3.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Remaining dependencies from the first Gradle file
    val coroutinesVersion = "1.7.3"
    val serializationVersion = "1.6.2"
    implementation("ch.acra:acra-mail:5.11.3")
    implementation("com.melnykov:floatingactionbutton:1.3.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.7.3")
    implementation("io.insert-koin:koin-android:3.5.3")
    implementation("androidx.fragment:fragment:1.6.2")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$coroutinesVersion")
    implementation("com.google.android.material:material:1.8.0")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("com.github.tony19:logback-android:2.0.1")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    testImplementation("net.wuerl.kotlin:assertj-core-kotlin:0.2.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("org.slf4j:slf4j-simple:2.0.11")

    val androidxTest = "1.5.0"
    androidTestImplementation("com.squareup.assertj:assertj-android:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:$androidxTest")
    androidTestImplementation("androidx.test:rules:$androidxTest")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}
