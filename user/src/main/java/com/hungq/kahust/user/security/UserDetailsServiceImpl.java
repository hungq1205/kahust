package com.hungq.kahust.user.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hungq.kahust.user.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo
			.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("Username of " + username + " not found!"));
	}
}
