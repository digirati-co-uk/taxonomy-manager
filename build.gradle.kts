import com.github.spotbugs.SpotBugsExtension
import com.github.spotbugs.SpotBugsTask

plugins {
    id("org.sonarqube") version "2.7"
    id("com.github.spotbugs") version "1.7.1" apply (false)
}

val ci: String by project
val isCiRunning = ci.toBoolean()

subprojects {
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "checkstyle")
    apply(plugin = "jacoco")

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.encoding = "UTF-8"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<Checkstyle> {
        reports {
            xml.isEnabled = isCiRunning
            html.isEnabled = !isCiRunning
        }
    }

    tasks.withType<SpotBugsTask> {
        reports {
            xml.isEnabled = isCiRunning
            html.isEnabled = !isCiRunning
        }
    }

    configure<SpotBugsExtension> {
        toolVersion = "3.1.+"
        effort = "max"
        isIgnoreFailures = true
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
