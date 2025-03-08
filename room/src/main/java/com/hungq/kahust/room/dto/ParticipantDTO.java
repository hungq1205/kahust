package com.hungq.kahust.room.dto;

public record ParticipantDTO(Long userId, String username, Long score, Integer answerIdx) {
	public ParticipantDTO(UserDTO user, Long score, Integer answerIdx) {
		this(user.id(), user.username(), score, answerIdx);
	}
}
