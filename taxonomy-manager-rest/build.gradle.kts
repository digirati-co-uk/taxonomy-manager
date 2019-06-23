plugins {
    `java-library`
}

dependencies {
    api("javax.ws.rs:javax.ws.rs-api:2.1.1")
    api("javax.validation:validation-api:2.0.1.Final")
    api(project(":taxonomy-manager-common"))
}
