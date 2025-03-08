package com.hungq.kahust.user.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;
	private static final Collection<GrantedAuthority> statisAuth = 
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @Column(unique = true, nullable = false)
	private String username;
    
    @Column(nullable = false)
	private String password;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return statisAuth;
	}
}
