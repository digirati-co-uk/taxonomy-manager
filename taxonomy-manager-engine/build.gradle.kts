plugins {
    `java-library`
}

dependencies {
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.7.1")

    implementation("org.ahocorasick", "ahocorasick", "0.4.0")
    implementation("com.google.guava", "guava", "27.1-jre")

    implementation("edu.stanford.nlp", "stanford-corenlp", "3.9.2")
    runtimeOnly("edu.stanford.nlp", "stanford-corenlp", "3.9.2", classifier = "models")

    testImplementation("org.junit.jupiter", "junit-jupiter", "5.4.2")
    testImplementation("org.hamcrest", "hamcrest", "2.1")
    testImplementation("org.mockito", "mockito-junit-jupiter", "2.27.0")

    api("org.apache.jena:jena-core:3.12.0")
    api("org.apache.jena:apache-jena-libs:3.12.0")
}
