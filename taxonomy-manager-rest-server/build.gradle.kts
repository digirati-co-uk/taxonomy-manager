import io.quarkus.gradle.tasks.QuarkusDev

plugins {
    java
    jacoco
    checkstyle
    id("io.quarkus") version "0.17.0"
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

sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTestRuntimeOnly by configurations.getting { extendsFrom(configurations.runtimeOnly.get()) }
val integrationTestImplementation by configurations.getting { extendsFrom(configurations.implementation.get()) }

configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dependencies {
    implementation(enforcedPlatform("io.quarkus:quarkus-bom:0.17.0"))

    quarkusExtensions.forEach { ext ->
        implementation("io.quarkus:quarkus-$ext:0.17.0")
    }
    implementation("org.springframework", "spring-jdbc", "5.1.3.RELEASE")
    implementation("org.json", "json", "20180813")

    implementation(project(":taxonomy-manager-common"))
    implementation(project(":taxonomy-manager-engine"))
    implementation(project(":taxonomy-manager-rest"))
    implementation("com.google.guava", "guava", "27.1-jre")

    testImplementation("io.quarkus", "quarkus-junit5", "0.17.0")

    integrationTestImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    integrationTestImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
    integrationTestImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.2")

    integrationTestImplementation("org.testcontainers:testcontainers:1.11.3")
    integrationTestImplementation("org.testcontainers:postgresql:1.11.3")
    integrationTestRuntimeOnly("org.postgresql:postgresql:42.2.5")
}

tasks {
    test {
        useJUnitPlatform()
    }
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

tasks.classes { dependsOn(mergeBuildOutput) }
tasks.check { dependsOn(integrationTest) }
