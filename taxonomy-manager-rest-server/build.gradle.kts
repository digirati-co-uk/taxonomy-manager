plugins {
    java
    jacoco
    checkstyle
    id("io.quarkus") version "0.16.1"
}


repositories {
    mavenCentral()
}

dependencies { //
    compile(enforcedPlatform("io.quarkus:quarkus-bom:0.16.1"))
    compile("io.quarkus:quarkus-resteasy")
}
