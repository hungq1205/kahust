package com.hungq.kahust.user.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
	private static final int EXPIRATION_MINS = 30;
	
	@Value("${jwt.secret}")
	private String secretKey;
	private SecretKey parsedSecretKey;

    @PostConstruct
    public void init() {
        parsedSecretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    
	public String generateToken(String userId) {
		return Jwts.builder()
			.setSubject(userId)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * EXPIRATION_MINS))
			.signWith(parsedSecretKey)
			.compact();
	}
}
