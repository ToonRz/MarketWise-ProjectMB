pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // <-- Add this line
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "MarketWise"
include(":app")
