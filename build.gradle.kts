import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "2.1.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
    idea
}

group = "bayern.kickner"
version = "1.6.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven("https://jitpack.io")
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("com.github.nexus421:KotNexLib:3.1.0")
    implementation("io.github.g0dkar:qrcode-kotlin:4.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

kotlin {
    jvmToolchain(17)
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

tasks.build {
    createBuildConfig()
}

fun Task.createBuildConfig() {
    val file = File("src/main/kotlin/BuildConfig.kt")
    outputs.file(file)
    file.writeText(
        """
        //AUTO-GENERATED THROUGH GRADLE. DO NOT CHANGE!
        //Will only be refreshed after a build after a gradle sync!
        
        val BUILD_TIMESTAMP = ${System.currentTimeMillis()}
        val VERSION = "$version"
    """.trimIndent()
    )
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            //https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/Native_distributions_and_local_execution/README.md
            modules("java.instrument", "jdk.httpserver", "jdk.unsupported")
            jvmArgs += listOf("-Xmx4G")
            packageName = "MultiDevTools"
            packageVersion = version.toString()
        }
    }
}
