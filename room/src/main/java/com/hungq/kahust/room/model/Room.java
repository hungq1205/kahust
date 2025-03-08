package com.hungq.kahust.room.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Room {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(unique=true, nullable=false)
	private Long id;
	
	@Column(unique=true, nullable=false)
	private Long ownerId;
	
	@Column(nullable=false)
	@Enumerated(EnumType.STRING) 
	private RoomState state;
	
	@Column(nullable=false)
	private Integer quizTimeout = 10;

	@Column(nullable=true)
	private Long quizSetId = null;
	
	@Column(nullable=false)
	private Integer currentQuizIdx = 0;
	
	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<Participant>();
}
