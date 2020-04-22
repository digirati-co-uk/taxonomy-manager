rootProject.name = "taxonomy-manager"

include("taxonomy-manager-common")
include("taxonomy-manager-engine")
include("taxonomy-manager-rest")
include("taxonomy-manager-rest-server")

// @TODO: Update this when the Quarkus gradle plugin is available on the Gradle Plugin Portal.
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
