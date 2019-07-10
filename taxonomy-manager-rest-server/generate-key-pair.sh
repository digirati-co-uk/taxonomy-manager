#!/usr/bin/env bash
openssl genpkey -algorithm RSA -out src/test/resources/privateTestKey.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in src/test/resources/privateTestKey.pem -out src/main/resources/META-INF/publicTestKey.pem
