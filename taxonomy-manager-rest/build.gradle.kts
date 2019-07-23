plugins {
    `java-library`
}

dependencies {
    api("javax.ws.rs:javax.ws.rs-api:2.1.1")
    api("javax.validation:validation-api:2.0.1.Final")
    api("javax.annotation:javax.annotation-api:1.3.2")
    api(project(":taxonomy-manager-common"))
    api("org.jboss.resteasy:resteasy-multipart-provider:4.0.0.Final")
}
