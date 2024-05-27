buildscript {
    repositories { google() }
    dependencies { dependencies { classpath("com.android.tools.build:gradle:7.3.1") } }
}


// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false

    jacoco
    id("com.diffplug.spotless") version "6.23.2"
    val kotlin = "1.9.22"
    kotlin("plugin.serialization") version kotlin apply false
}



