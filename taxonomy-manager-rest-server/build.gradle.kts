plugins {
    java
    jacoco
    checkstyle
    id("io.quarkus") version "0.16.1"
}


repositories {
    mavenCentral()
}

val quarkusExtensions = setOf(
        "agroal",
        "resteasy",
        "resteasy-jsonb",
        "jdbc-postgresql"
)

dependencies {
    compile(enforcedPlatform("io.quarkus:quarkus-bom:0.16.1"))
    compile(project(":taxonomy-manager-engine"))

    quarkusExtensions.forEach { ext ->
        compile("io.quarkus:quarkus-$ext")
    }

    testCompile("io.quarkus", "quarkus-junit5")
    testCompile("io.rest-assured", "rest-assured")
}
