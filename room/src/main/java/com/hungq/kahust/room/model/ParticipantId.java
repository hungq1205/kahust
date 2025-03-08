package com.hungq.kahust.room.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
public class ParticipantId implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "room_id")
    private Long roomId;
}