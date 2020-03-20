import io.quarkus.gradle.tasks.QuarkusDev
import org.testcontainers.containers.PostgreSQLContainer

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("gradle.plugin.io.quarkus:quarkus-gradle-plugin:1.3.0.Final")
        classpath("org.testcontainers:testcontainers:1.11.3")
        classpath("org.testcontainers:postgresql:1.11.3")
        classpath("org.postgresql:postgresql:42.2.5")
    }
}

plugins {
    java
    jacoco
    checkstyle
}

apply(plugin = "io.quarkus")

repositories {
    mavenCentral()
}

val quarkusExtensions = setOf(
        "agroal",
        "flyway",
        "hibernate-validator",
        "jdbc-postgresql",
        "resteasy",
        "resteasy-jsonb",
        "smallrye-jwt",
        "smallrye-health"
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

dependencies {
    implementation(enforcedPlatform("io.quarkus:quarkus-bom:1.3.0.Final"))

    quarkusExtensions.forEach { ext ->
        implementation("io.quarkus:quarkus-$ext")
    }

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

        doFirst {
            // Kotlin doesn't support initialization of types with self-referential generics,
            // so we resort to reflection
            var testDatabaseContainer = PostgreSQLContainer::class.java
                    .getDeclaredConstructor(String::class.java)
                    .newInstance("postgres:11")

            testDatabaseContainer.start()

            systemProperty("quarkus.datasource.url", testDatabaseContainer.jdbcUrl)
            systemProperty("quarkus.datasource.username", testDatabaseContainer.username)
            systemProperty("quarkus.datasource.password", testDatabaseContainer.password)
        }
    }
}

tasks.withType<QuarkusDev> {
}

val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    reports {
        junitXml.isEnabled = true
    }

    testLogging.showStandardStreams = true
}

val mergeBuildOutput = task<Copy>("mergeBuildOutput") {
    fun getOutputs(project: Project) = project.sourceSets.getByName("main").output

    from(getOutputs(project(":taxonomy-manager-common")))
    from(getOutputs(project(":taxonomy-manager-engine")))
    from(getOutputs(project(":taxonomy-manager-rest")))

    into("$buildDir/classes/java/main/")
}

val generateTestKeyPair = task<Exec>("generateTestKeyPair") {
    commandLine = listOf("sh", "$projectDir/generate-key-pair.sh")
}

tasks.classes { dependsOn(mergeBuildOutput) }
tasks.check { dependsOn(integrationTest) }
