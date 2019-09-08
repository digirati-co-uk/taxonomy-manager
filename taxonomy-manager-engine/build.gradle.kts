plugins {
    `java-library`
    id("me.champeau.gradle.jmh") version "0.5.0-rc-2"
}

dependencies {
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.7.1")

    api("com.google.guava", "guava", "27.1-jre")
    api("edu.stanford.nlp", "stanford-corenlp", "3.9.2")
    api("edu.stanford.nlp", "stanford-corenlp", "3.9.2", classifier = "models")

    testImplementation("org.junit.jupiter", "junit-jupiter", "5.4.2")
}

jmh {
    duplicateClassesStrategy = DuplicatesStrategy.WARN
}
