pluginManagement {
    repositories {
        mavenCentral()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.geckolib.com/")
        maven("https://jitpack.io")
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "net.neoforged.gradle") {
                useModule("net.neoforged:neoforge-gradle-plugin:1.2.0")
            }
        }
    }
}

rootProject.name = "HulkMod"