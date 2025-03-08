package com.hungq.kahust.room.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hungq.kahust.room.dto.UserBatchRequest;
import com.hungq.kahust.room.dto.UserDTO;

@Service
public class UserServiceClient {
	private final WebClient client;
	private final String userServiceUrl = "http://user-service:3003";
	
	public UserServiceClient(WebClient.Builder builder) {
		this.client = builder.baseUrl(userServiceUrl).build();
	}

    public UserDTO getUserById(Long userId) {
        return client.get()
            .uri("/users/" + userId)
            .retrieve()
            .bodyToMono(UserDTO.class)
            .block();
    }

    public List<UserDTO> getUsersByIds(List<Long> userIds) {
        return client.post()
            .uri("/users/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserBatchRequest(userIds))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserDTO>>() {})
            .block();
    }
}
