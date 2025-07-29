//package com.cruzze.util;
//
//import com.nimbusds.jose.jwk.source.RemoteJWKSet;
//import com.nimbusds.jose.jwk.source.JWKSource;
//import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
//import com.nimbusds.jwt.proc.DefaultJWTProcessor;
//import com.nimbusds.jose.proc.SecurityContext;
//import com.nimbusds.jose.proc.JWSVerificationKeySelector;
//import com.nimbusds.jose.JWSAlgorithm;
//
//import java.net.URL;
//import java.util.Map;
//
//public class JwtUtils {
//
//    private static final String CLERK_JWKS_URL = "https://useful-flamingo-41.clerk.accounts.dev/.well-known/jwks.json";
//
//    public static Map<String, Object> verifyAndExtractPayload(String token) throws Exception {
//        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
//        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(CLERK_JWKS_URL));
//        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
//
//        jwtProcessor.setJWSKeySelector(new JWSVerificationKeySelector<>(expectedJWSAlg, keySource));
//
//        try {
//            System.out.println("⏳ Verifying JWT token...");
//            var claimsSet = jwtProcessor.process(token, null);
//            System.out.println("✅ Claims: " + claimsSet.getClaims());
//            return claimsSet.getClaims();
//        } catch (Exception e) {
//            System.err.println("❌ Failed to verify JWT token: " + e.getMessage());
//            e.printStackTrace();
//            throw new Exception("Invalid or expired JWT token", e);
//        }
//    }
//
//}












package com.cruzze.util;

import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.JWSAlgorithm;

import java.net.URL;
import java.util.Map;

public class JwtUtils {

    // Make this configurable via environment variable
    private static final String CLERK_JWKS_URL = System.getenv("CLERK_JWKS_URL") != null ? 
        System.getenv("CLERK_JWKS_URL") : 
        "https://useful-flamingo-41.clerk.accounts.dev/.well-known/jwks.json";

    public static Map<String, Object> verifyAndExtractPayload(String token) throws Exception {
        if (token == null || token.trim().isEmpty()) {
            throw new Exception("Token is null or empty");
        }

        System.out.println("🔍 JWT Utils - Starting token verification...");
        System.out.println("🔍 JWT Utils - Using JWKS URL: " + CLERK_JWKS_URL);
        System.out.println("🔍 JWT Utils - Token length: " + token.length());

        try {
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(CLERK_JWKS_URL));
            JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

            jwtProcessor.setJWSKeySelector(new JWSVerificationKeySelector<>(expectedJWSAlg, keySource));

            System.out.println("⏳ JWT Utils - Verifying JWT token...");
            var claimsSet = jwtProcessor.process(token, null);
            
            Map<String, Object> claims = claimsSet.getClaims();
            System.out.println("✅ JWT Utils - Token verified successfully");
            System.out.println("✅ JWT Utils - Claims extracted: " + claims);
            
            // Validate required claims
            if (!claims.containsKey("sub")) {
                throw new Exception("Token missing required 'sub' claim");
            }
            
            String sub = (String) claims.get("sub");
            if (sub == null || sub.trim().isEmpty()) {
                throw new Exception("Token 'sub' claim is null or empty");
            }
            
            System.out.println("✅ JWT Utils - Required claims validated");
            return claims;
            
        } catch (Exception e) {
            System.err.println("❌ JWT Utils - Failed to verify JWT token: " + e.getMessage());
            System.err.println("❌ JWT Utils - Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            throw new Exception("Invalid or expired JWT token: " + e.getMessage(), e);
        }
    }
}
