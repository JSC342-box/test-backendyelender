package com.cruzze.util;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jose.JWSAlgorithm;

import java.net.URL;
import java.util.Map;

public class JwtUtils {

    private static final String CLERK_JWKS_URL = "https://api.clerk.dev/.well-known/jwks.json";

    public static Map<String, Object> verifyAndExtractPayload(String token) throws Exception {
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(CLERK_JWKS_URL));
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

        jwtProcessor.setJWSKeySelector(new JWSVerificationKeySelector<>(expectedJWSAlg, keySource));
        return jwtProcessor.process(token, null).getClaims();
    }
}
