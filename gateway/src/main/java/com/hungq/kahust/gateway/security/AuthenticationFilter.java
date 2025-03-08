package com.hungq.kahust.gateway.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	
	@Override
	public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) 
		throws ServletException, IOException 
	{
		String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			chain.doFilter(req, res);
			return;
		}
		
		String token = authHeader.substring(7);
		if (!jwtUtil.validateToken(token)) {
			chain.doFilter(req, res);
			return;
		}
		
		String userId = jwtUtil.extractUserId(token);
			
		UserDetails userDetails = User.withUsername(userId).password("").authorities("USER").build();
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
			
		chain.doFilter(new RequestWrapper(req, userId), res);
	}

    private static class RequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String> headers = new HashMap<>();

        RequestWrapper(HttpServletRequest request, String userId) {
            super(request);
            headers.put("User-ID", userId);
        }

        @Override public String getHeader(String name) { return headers.getOrDefault(name, super.getHeader(name)); }
        @Override public Enumeration<String> getHeaderNames() { return Collections.enumeration(headers.keySet()); }
        @Override public Enumeration<String> getHeaders(String name) {
            return headers.containsKey(name) ? Collections.enumeration(Collections.singletonList(headers.get(name))) : super.getHeaders(name);
        }
    }
}
