rootProject.name = "digirati-taxonomy-manager"

include("taxonomy-manager-engine")
include("taxonomy-manager-rest-server")

/* Update this when the Quarkus gradle plugin is available on the Gradle Plugin Portal. */
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "io.quarkus") {
                useModule("io.quarkus:quarkus-gradle-plugin:${requested.version}")
            }
        }
    }
}
