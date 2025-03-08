package com.hungq.kahust.room.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.hungq.kahust.room.model.Room;
import com.hungq.kahust.room.model.RoomState;
import com.hungq.kahust.room.service.UserServiceClient;

public record RoomDTO(
	    Long id,
	    Long ownerId,
	    RoomState state,
	    Integer quizTimeout,
	    Long quizSetId,
	    Integer currentQuizIdx,
	    List<ParticipantDTO> participants
) {
    public RoomDTO(Room room, UserServiceClient userServiceClient) {
        this(
            room.getId(),
            room.getOwnerId(),
            room.getState(),
            room.getQuizTimeout(),
            room.getQuizSetId(),
            room.getCurrentQuizIdx(),
            room.getParticipants().stream()
                .map(p -> {
                    UserDTO user = userServiceClient.getUserById(p.getId().getUserId());
                    return new ParticipantDTO(user, p.getScore(), p.getAnswerIdx());
                })
                .collect(Collectors.toList())
        );
    }
}

