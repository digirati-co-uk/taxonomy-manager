buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:2.7")
        classpath("gradle.plugin.com.github.spotbugs:spotbugs-gradle-plugin:1.7.1")
        classpath("gradle.plugin.com.simonscholz:report-aggregator-gradle:0.0.1")
    }
}

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
