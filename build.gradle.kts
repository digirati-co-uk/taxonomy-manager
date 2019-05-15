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

dependencies {
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.7.1")

    compile("org.ahocorasick", "ahocorasick", "0.4.0")
    compile("com.google.guava", "guava", "27.1-jre")

    testCompile("org.junit.jupiter", "junit-jupiter", "5.4.2")
    testCompile("org.hamcrest", "hamcrest", "2.1")
    testCompile("org.mockito", "mockito-junit-jupiter", "2.27.0")
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
