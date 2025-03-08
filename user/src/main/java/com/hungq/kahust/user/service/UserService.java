package com.hungq.kahust.user.service;

import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hungq.kahust.user.dto.UserDTO;
import com.hungq.kahust.user.model.User;
import com.hungq.kahust.user.repository.UserRepository;
import com.hungq.kahust.user.security.JwtUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepo;
	private final AuthenticationManager authManager;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	
	public List<UserDTO> findAll() {
		return userRepo.findAll().stream().map(UserDTO::new).toList();
	}

    public List<UserDTO> getUsersByIds(List<Long> userIds) {
        return userRepo.findAllById(userIds)
                .stream()
                .map(UserDTO::new)
                .toList();
    }
    
	public Optional<UserDTO> findById(Long id) {
		return userRepo.findById(id).map(UserDTO::new);
	}
	
	public Optional<UserDTO> findByUsername(String username) {
		return userRepo.findByUsername(username).map(UserDTO::new);
	}

	public String authenticate(String username, String password) throws AuthenticationException {
		authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		User user = userRepo.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return jwtUtil.generateToken(user.getId().toString());
	}

	public UserDTO createUser(String username, String password) throws BadRequestException {
		if (userRepo.findByUsername(username).isPresent())
			throw new BadRequestException("Username already exists");
		
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		
		return new UserDTO(userRepo.save(user));
	}
	
	public UserDTO updateUser(String username, String password) {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		user.setPassword(passwordEncoder.encode(password));
		return new UserDTO(userRepo.save(user));
	}
}
