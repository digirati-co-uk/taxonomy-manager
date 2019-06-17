plugins {
    `java-library`
}

dependencies {
    implementation("com.google.guava", "guava", "27.1-jre")

    api("org.apache.jena:jena-core:3.12.0")
    api("org.apache.jena:apache-jena-libs:3.12.0")
}
