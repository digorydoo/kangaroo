plugins {
    kotlin("multiplatform") version "1.7.20"
}

group = "me.pamberg"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")

    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        val main by compilations.getting
        val interop by main.cinterops.creating

        binaries {
            // executable { entryPoint = "main" }
            staticLib { baseName = "native" }
        }
    }

    sourceSets {
        val nativeMain by getting
        // val nativeTest by getting
    }
}
