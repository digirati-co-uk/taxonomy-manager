package com.digirati.taxman.rest.server.infrastructure.auth;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.jwt.Claims;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import static net.minidev.json.parser.JSONParser.DEFAULT_PERMISSIVE_MODE;

public class JwtGenerator {

    private static final String CLAIMS_JSON = "/jwtClaims.json";
    private static final long TIMEOUT_SECONDS = 18000000000000L; // 5h
    private static final String PRIVATE_KEY_PATH = "/privateTestKey.pem";

    public static void main(String[] args) throws Exception {
        PrivateKey pk = loadPrivateKey();
        JSONObject jwtContent = buildClaims();

        JWSSigner signer = new RSASSASigner(pk);
        JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwtContent);
        JWSAlgorithm alg = JWSAlgorithm.RS256;
        JWSHeader jwtHeader = new JWSHeader.Builder(alg)
                .keyID(JwtGenerator.PRIVATE_KEY_PATH)
                .type(JOSEObjectType.JWT)
                .build();
        SignedJWT signedJWT = new SignedJWT(jwtHeader, claimsSet);
        signedJWT.sign(signer);

        System.out.println(signedJWT.serialize());
    }

    private static PrivateKey loadPrivateKey() throws Exception {
        InputStream privateKeyInputStream = JwtGenerator.class.getResourceAsStream(PRIVATE_KEY_PATH);
        String privateKey = IOUtils.toString(privateKeyInputStream, StandardCharsets.UTF_8);
        privateKey = privateKey.replaceAll("-----BEGIN (.*)-----", "");
        privateKey = privateKey.replaceAll("-----END (.*)----", "");
        privateKey = privateKey.replaceAll("\r\n", "");
        privateKey = privateKey.replaceAll("\n", "");
        String normalisedPrivateKey = privateKey.trim();
        byte[] encodedBytes = Base64.getDecoder().decode(normalisedPrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    private static JSONObject buildClaims() throws Exception {
        InputStream contentIS = JwtGenerator.class.getResourceAsStream(JwtGenerator.CLAIMS_JSON);
        if (contentIS == null) {
            throw new IllegalStateException("Failed to find resource: " + JwtGenerator.CLAIMS_JSON);
        }
        JSONParser parser = new JSONParser(DEFAULT_PERMISSIVE_MODE);
        JSONObject jwtContent = parser.parse(contentIS.readAllBytes(), JSONObject.class);
        long currentTimeInSecs = (int) (System.currentTimeMillis() / 1000);
        long exp = currentTimeInSecs + TIMEOUT_SECONDS;
        System.out.printf("Setting exp: %d / %s%n", exp, new Date(1000*exp));
        jwtContent.put(Claims.exp.name(), exp);
        jwtContent.put(Claims.iat.name(), currentTimeInSecs);
        jwtContent.put(Claims.auth_time.name(), currentTimeInSecs);
        return jwtContent;
    }
}
