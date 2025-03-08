package com.hungq.kahust.gateway.security;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secretKey;
	private SecretKey parsedSecretKey;

    @PostConstruct
    public void init() {
        parsedSecretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }
	
	public String extractUserId(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(parsedSecretKey)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts
				.parserBuilder()
				.setSigningKey(parsedSecretKey)
				.build()
				.parseClaimsJws(token);
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
		
		return true;
	}
}
