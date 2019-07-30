#!/bin/bash

set -o errexit

main() {
    set_java_opts
	run_backend "$@"
}

set_java_opts() {
    JAVA_OPTS="$JAVA_OPTS \
        -Dquarkus.datasource.url=${QUARKUS_DATASOURCE_URL} \
        -Dquarkus.datasource.driver=${QUARKUS_DATASOURCE_DRIVER} \
        -Dquarkus.datasource.username=${QUARKUS_DATASOURCE_USERNAME} \
        -Dquarkus.datasource.password=${QUARKUS_DATASOURCE_PASSWORD} \
        -Dmp.jwt.verify.publickey.location='' \
        -Dmp.jwt.verify.publickey=${MP_JWT_VERIFY_PUBLICKEY} \
        -Dmp.jwt.verify.issuer=${MP_JWT_VERIFY_ISSUER} \
        -Dquarkus.http.cors.origins=${QUARKUS_HTTP_CORS_ORIGINS}"
}

run_backend() {
	set -e
	exec "$@"
}

main "$@"
