package com.hungq.kahust.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hungq.kahust.user.dto.BatchUserRequest;
import com.hungq.kahust.user.dto.UserDTO;
import com.hungq.kahust.user.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	@GetMapping("")
	public ResponseEntity<List<UserDTO>> getUsers() {
		return ResponseEntity.ok(userService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
		return userService.findById(id)
			.map(ResponseEntity::ok)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("User-ID") Long id) {
		return userService.findById(id)
				.map(ResponseEntity::ok)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}
	
	@GetMapping("/username/{username}")
	public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
		return userService.findByUsername(username)
			.map(ResponseEntity::ok)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}
	
    @PostMapping("/batch")
    public ResponseEntity<List<UserDTO>> getUsersByIds(@RequestBody BatchUserRequest req) {
        List<UserDTO> users = userService.getUsersByIds(req.userIds());
        return ResponseEntity.ok(users);
    }
}
