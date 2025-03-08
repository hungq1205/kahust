package com.hungq.kahust.user.dto;

import com.hungq.kahust.user.model.User;

public record UserDTO(Long id, String username) {
	public UserDTO(User user) {
		this(user.getId(), user.getUsername());
	}
}
