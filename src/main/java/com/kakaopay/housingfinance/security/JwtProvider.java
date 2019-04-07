package com.kakaopay.housingfinance.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    private Clock clock = DefaultClock.INSTANCE;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    public String generateToken(CustomUserDetails user) {
        Date issuedDate = clock.now();
        Date expiryDate = new Date(issuedDate.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(Long.toString(user.getId()))
                .setClaims(setClaims(user))
                .setIssuedAt(issuedDate)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, getBase64EncodedSecretKey())
                .compact();
    }

    private Claims setClaims(CustomUserDetails user) {
        Claims claims = new DefaultClaims();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("roles", String.join(",", AuthorityUtils.authorityListToSet(user.getAuthorities())));

        return claims;
    }

    private String getBase64EncodedSecretKey() {
        return Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }

    public Jws<Claims> getAllClaims(String token) {
        return Jwts.parser().setSigningKey(getBase64EncodedSecretKey()).parseClaimsJws(token);
    }
}