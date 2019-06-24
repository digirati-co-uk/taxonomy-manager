import io.quarkus.gradle.tasks.QuarkusDev

plugins {
    java
    jacoco
    checkstyle
    id("io.quarkus") version "0.16.1"
}


repositories {
    mavenCentral()
}

val quarkusExtensions = setOf(
        "agroal",
        "flyway",
        "hibernate-validator",
        "jdbc-postgresql",
        "resteasy",
        "resteasy-jsonb"
)

java {
    sourceSets.getByName("main").output.setResourcesDir("build/classes/java/main")
}

dependencies {
    implementation(enforcedPlatform("io.quarkus:quarkus-bom:0.16.1"))

    quarkusExtensions.forEach { ext ->
        implementation("io.quarkus:quarkus-$ext:0.16.1")
    }
    implementation("org.springframework", "spring-jdbc", "5.1.3.RELEASE")
    implementation("org.json", "json", "20180813")

    implementation(project(":taxonomy-manager-common"))
    implementation(project(":taxonomy-manager-engine"))
    implementation(project(":taxonomy-manager-rest"))
    implementation("com.google.guava", "guava", "27.1-jre")

    testImplementation("org.testcontainers:testcontainers:1.11.3")
    testImplementation("org.testcontainers:postgresql:1.11.3")
    testRuntimeOnly("org.postgresql:postgresql:42.2.5")

    testImplementation("io.quarkus", "quarkus-junit5", "0.16.1")
}

tasks {
    test {                                  // (5)
        useJUnitPlatform() {
            excludeTags("integration")
        }
    }

    create<Test>("integrationTest") {
        useJUnitPlatform {
            includeTags("integration")
        }
    }
}

fun getOutputs(project: Project) = project.sourceSets.getByName("main").output

// TODO: This is a temporary fix for Quarkus Devtools being unable to recognize
// the classpath of projects in a multi-module Gradle build.
tasks.register("mergeBuildOutput") {
    doLast {
        copy {
            from(getOutputs(project(":taxonomy-manager-common")))
            from(getOutputs(project(":taxonomy-manager-engine")))
            from(getOutputs(project(":taxonomy-manager-rest")))

            into("$buildDir/classes/java/main/")
        }
    }
}

tasks["classes"].dependsOn("mergeBuildOutput")
