package com.hungq.kahust.room.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Participant {
    @EmbeddedId
    private ParticipantId id;

	@Column(nullable=false)
	private Long score = 0L;
	
	@Column(nullable=false)
	private Integer answerIdx = -1;
	
	@ManyToOne
    @MapsId("roomId")
    @JoinColumn(name = "room_id", referencedColumnName = "id", nullable = false)
    private Room room;
}
