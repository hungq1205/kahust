package com.hungq.kahust.user.controller;

import java.net.URI;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hungq.kahust.user.dto.AuthRequestDTO;
import com.hungq.kahust.user.dto.AuthResponseDTO;
import com.hungq.kahust.user.dto.UserDTO;
import com.hungq.kahust.user.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
	private final UserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO authReq) {
		try {
			String token = userService.authenticate(authReq.username(), authReq.password());			
			return ResponseEntity.ok(new AuthResponseDTO(token));
		} catch (AuthenticationException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password");
		}
	}
	
	@PostMapping("/signup")
	public ResponseEntity<UserDTO> signup(@RequestBody AuthRequestDTO authReq) {
		try {
			UserDTO user = userService.createUser(authReq.username(), authReq.password());
			URI location = URI.create("/users/" + user.id());
			return ResponseEntity.created(location).body(user);
		} catch (BadRequestException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
		}
	}
}
