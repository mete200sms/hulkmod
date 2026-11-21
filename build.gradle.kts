import org.gradle.kotlin.dsl.`kotlin-dsl`
import java.io.File

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("com.mojang.datafixerupper") version "1.0.0"
    id("net.neoforged.gradle") version "1.2.0"
    id("com.github.jengelman.gradle.plugins.shadow") version "8.1.1"
}

group = "com.hulkmod"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.geckolib.com/")
    maven("https://jitpack.io")
}

dependencies {
    minecraft("net.neoforged:neoforge:1.21.1-55.0.40")
    implementation("com.geckolib:geckolib-neo:1.21.1-4.2.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = 21
    options.compilerArgs << "-Xlint:unchecked" << "-Xlint:deprecation"
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs = listOf("-Xjvm-default=all", "-Xopt-in=kotlin.RequiresOptIn")
    }
}

tasks.named("jar") {
    manifest {
        attributes(
            "Implementation-Title" to "Hulk Mod",
            "Implementation-Version" to project.version.toString(),
            "Main-Class" to "com.hulkmod.HulkMod",
            "NeoForge-Mod" to "true"
        )
    }
    from(sourceSets.main.get().output)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named("shadowJar") {
    archiveClassifier.set("")
    manifest {
        attributes(
            "Implementation-Title" to "Hulk Mod",
            "Implementation-Version" to project.version.toString(),
            "Main-Class" to "com.hulkmod.HulkMod",
            "NeoForge-Mod" to "true"
        )
    }
}

tasks.named("build") {
    finalizedBy("shadowJar")
}

neoforge {
    mappings = "official"
    modLoader = "neoforge"
}