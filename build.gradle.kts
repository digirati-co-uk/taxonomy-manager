repositories {
    mavenCentral()
}

plugins {
    java
    jacoco
    checkstyle
    id("org.sonarqube") version "2.7"
    id("com.github.spotbugs") version "1.7.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val ahoCorasickVersion: String by project
val guavaVersion: String by project
val jenaVersion: String by project
val log4jVersion: String by project
val jacksonVersion: String by project
val junitVersion: String by project
val hamcrestVersion: String by project
val mockitoVersion: String by project

dependencies {
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.7.1")

    compile("org.ahocorasick", "ahocorasick", ahoCorasickVersion)
    compile("com.google.guava", "guava", guavaVersion)
    compile("org.apache.jena", "jena-core", jenaVersion)
    compile("org.apache.jena", "apache-jena-libs", jenaVersion)
    compile("org.apache.logging.log4j", "log4j-core", log4jVersion)
    compile("org.apache.logging.log4j", "log4j-api", log4jVersion)
    compile("com.fasterxml.jackson.core", "jackson-databind", jacksonVersion)

    testCompile("org.junit.jupiter", "junit-jupiter", junitVersion)
    testCompile("org.hamcrest", "hamcrest", hamcrestVersion)
    testCompile("org.mockito", "mockito-junit-jupiter", mockitoVersion)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

spotbugs {
    toolVersion = "3.1.+"
    effort = "max"
    isIgnoreFailures = true
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.isEnabled = false
        html.isEnabled = true
    }
}

sonarqube {
    properties {
        property("sonar.organization", "digirati")
        property("sonar.projectName", "digirati-taxonomy-manager")
        property("sonar.projectKey", "digirati-co-uk_digirati-taxonomy-manager")
        property("sonar.pullrequest.provider", "GitHub")
        property("sonar.pullrequest.github.repository", "digirati-co-uk/digirati-taxonomy-manager")
    }
}
