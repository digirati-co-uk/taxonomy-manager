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
        -Dquarkus.flyway.migrate-at-start=${QUARKUS_FLYWAY_MIGRATE_AT_START} \
        -Dmp.jwt.verify.publickey.location=${MP_JWT_VERIFY_PUBLICKEY_LOCATION} \
        -Dmp.jwt.verify.issuer=${MP_JWT_VERIFY_ISSUER}"
}

run_backend() {
	set -e
	exec "$@"
}

main "$@"
