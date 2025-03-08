package com.hungq.kahust.room.dto;

import com.hungq.kahust.room.model.RoomState;

public record RoomUpdateRequest(RoomState state, Long quizSetId, Integer quizIdx, Integer quizTimeout) {}
