plugins {
    `java-library`
}

dependencies {
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.7.1")

    api("com.google.guava", "guava", "27.1-jre")
    api("edu.stanford.nlp", "stanford-corenlp", "3.9.2")
    api("edu.stanford.nlp", "stanford-corenlp", "3.9.2", classifier = "models")

    testImplementation("org.junit.jupiter", "junit-jupiter", "5.4.2")
}

}
