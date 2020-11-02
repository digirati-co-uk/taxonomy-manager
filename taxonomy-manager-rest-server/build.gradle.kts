import io.quarkus.gradle.tasks.QuarkusDev
import org.testcontainers.containers.PostgreSQLContainer

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.google.cloud.tools:jib-quarkus-extension-gradle:0.1.1")
        classpath("org.testcontainers:testcontainers:1.11.3")
        classpath("org.testcontainers:postgresql:1.11.3")
        classpath("org.postgresql:postgresql:42.2.5")
    }
}

plugins {
    java
    jacoco
    checkstyle
    id ("io.quarkus") version ("1.3.1.Final")
    id ("com.google.cloud.tools.jib")
}

repositories {
    mavenCentral()
}

jib {
    container {
        mainClass = "io.quarkus.runner.GeneratedMain"
        jvmFlags = listOf("-Dquarkus.http.host=0.0.0.0", "-Djava.util.logging.manager=org.jboss.logmanager.LogManager")
        user = "1000"
    }

    to {
        image = "taxonomy-manager:latest"
    }

    pluginExtensions {
        pluginExtension {
            implementation = "com.google.cloud.tools.jib.gradle.extension.quarkus.JibQuarkusExtension"
        }
    }
}

val quarkusExtensions = setOf(
        "agroal",
        "flyway",
        "hibernate-validator",
        "jdbc-postgresql",
        "resteasy",
        "resteasy-jsonb",
        "smallrye-jwt",
        "smallrye-health",
        "smallrye-reactive-messaging",
        "smallrye-reactive-messaging-amqp"
)

java {
    sourceSets.getByName("main").output.setResourcesDir("build/classes/java/main")
}

sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTestRuntimeOnly by configurations.getting { extendsFrom(configurations.testRuntimeOnly.get()) }
val integrationTestImplementation by configurations.getting { extendsFrom(configurations.testImplementation.get()) }

configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

quarkus {
    setFinalName("taxonomy-manager-rest-server")
}

dependencies {
    implementation(enforcedPlatform("io.quarkus:quarkus-bom:1.3.1.Final"))

    quarkusExtensions.forEach { ext ->
        implementation("io.quarkus:quarkus-$ext:1.3.1.Final")
    }

    implementation("org.jgroups.quarkus.extension:quarkus-jgroups:1.0.1.Final")
    implementation("org.springframework", "spring-jdbc", "5.1.3.RELEASE")
    implementation("org.json", "json", "20180813")

    implementation(project(":taxonomy-manager-common"))
    implementation(project(":taxonomy-manager-engine"))
    implementation(project(":taxonomy-manager-rest"))
    implementation("com.google.guava", "guava", "27.1-jre")

    testImplementation("io.rest-assured:rest-assured:3.3.0")
    testImplementation("io.quarkus", "quarkus-junit5")
    testImplementation("com.nimbusds", "nimbus-jose-jwt", "7.4")
    testCompileOnly("org.jetbrains:annotations:17.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    integrationTestImplementation("org.testcontainers:testcontainers:1.11.3")
    integrationTestImplementation("org.testcontainers:postgresql:1.11.3")
    integrationTestRuntimeOnly("org.postgresql:postgresql:42.2.5")
}

tasks {
    getByName("testNative") {
        enabled = false
    }

    getByName("buildNative") {
        enabled = false
    }

    test {
        useJUnitPlatform()
        setForkEvery(1)
    }
}

tasks.withType<QuarkusDev> {
    jvmArgs = "-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false"
}

val generateTestKeyPair = task<Exec>("generateTestKeyPair") {
    commandLine = listOf("sh", "$projectDir/generate-key-pair.sh")
}

tasks.jib { dependsOn(tasks.quarkusBuild) }
