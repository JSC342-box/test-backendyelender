//package com.cruzze.security;
//
//import com.cruzze.util.JwtUtils;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.Map;
//
//@Component
//public class ClerkJwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Missing or invalid Authorization header");
//            return;
//        }
//        
//        try {
//            String token = authHeader.replace("Bearer ", "");
//            Map<String, Object> claims = JwtUtils.verifyAndExtractPayload(token);
//
//            String clerkUserId = (String) claims.get("sub");
//
//            // Set authentication in context
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    clerkUserId,
//                    null,
//                    Collections.emptyList()
//            );
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Invalid or expired Clerk token");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
//






















package com.cruzze.security;

import com.cruzze.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
public class ClerkJwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String requestPath = request.getRequestURI();
        String userAgent = request.getHeader("User-Agent");

        // Log request details for debugging
        System.out.println("🔍 Auth Filter - Path: " + requestPath);
        System.out.println("🔍 Auth Filter - User-Agent: " + userAgent);
        System.out.println("🔍 Auth Filter - Authorization header present: " + (authHeader != null));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Auth Filter - Missing or invalid Authorization header");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }
        
        try {
            String token = authHeader.replace("Bearer ", "");
            System.out.println("🔍 Auth Filter - Token length: " + token.length());
            System.out.println("🔍 Auth Filter - Token prefix: " + token.substring(0, Math.min(20, token.length())) + "...");
            
            Map<String, Object> claims = JwtUtils.verifyAndExtractPayload(token);

            String clerkUserId = (String) claims.get("sub");
            String userType = (String) claims.get("userType");
            
            System.out.println("✅ Auth Filter - JWT verified successfully");
            System.out.println("✅ Auth Filter - Clerk User ID: " + clerkUserId);
            System.out.println("✅ Auth Filter - User Type: " + userType);

            // Set authentication in context
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    clerkUserId,
                    null,
                    Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            System.out.println("✅ Auth Filter - Authentication set in SecurityContext");
        } catch (Exception e) {
            System.err.println("❌ Auth Filter - JWT verification failed: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired Clerk token: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
