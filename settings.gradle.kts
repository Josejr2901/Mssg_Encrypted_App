pluginManagement {
    repositories {
        google() // ✅ Ensure Google repository is added
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // ✅ Ensure Google repository is added
        mavenCentral()
    }
}

rootProject.name = "EncryptedMessagingApp"
include(":app")
