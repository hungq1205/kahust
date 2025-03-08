package com.hungq.kahust.room.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hungq.kahust.room.dto.ParticipantDTO;
import com.hungq.kahust.room.dto.QuizAnswerResponse;
import com.hungq.kahust.room.dto.RoomDTO;
import com.hungq.kahust.room.dto.RoomUpdateRequest;
import com.hungq.kahust.room.dto.UserDTO;
import com.hungq.kahust.room.model.Participant;
import com.hungq.kahust.room.model.ParticipantId;
import com.hungq.kahust.room.model.Room;
import com.hungq.kahust.room.model.RoomState;
import com.hungq.kahust.room.repository.ParticipantRepository;
import com.hungq.kahust.room.repository.RoomRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RoomService {
	private final RoomRepository roomRepo;
	private final ParticipantRepository participantRepo;
	private final UserServiceClient userServiceClient;
	private final QuizServiceClient quizServiceClient;
	
	public Boolean isOwner(Long roomId, Long userId) {
		return roomRepo.findById(roomId)
				.map(room -> room.getOwnerId().equals(userId))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found"));
	}
	
	public Boolean isParticipant(Long roomId, Long userId) {
		return participantRepo.findById(new ParticipantId(userId, roomId)).isEmpty();
	}
	
	public List<Room> findRooms() {
		return roomRepo.findAll();
	}
	
	public Optional<Room> findRoomById(Long id) {
		return roomRepo.findById(id);
	}
	
	public Optional<Room> findRoomOfUser(Long userId) {
		return participantRepo.findByUserId(userId).map(Participant::getRoom);
	}

	public void resetAnswersInRoom(Long roomId) {
		participantRepo.resetAnswersByRoomId(roomId);
	}

	public QuizAnswerResponse updateParticipantAnswer(Long userId, Long quizId, Integer answerIdx) {
		Participant p = participantRepo.findByUserId(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant not found"));
		if (p.getAnswerIdx() != -1)
			return new QuizAnswerResponse(false);
		p.setAnswerIdx(answerIdx);
		participantRepo.save(p);
		return quizServiceClient.checkAnswer(quizId, answerIdx);
	}
	
	public void resetScoresInRoom(Long roomId) {
		participantRepo.resetScoresByRoomId(roomId);
	}

	public void updateAddParticipantScore(Long userId, Long score) {
		Participant p = participantRepo.findByUserId(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant not found"));
		p.setScore(p.getScore() + score);
		participantRepo.save(p);
	}
	
	public Room updateRoom(Long roomId, RoomUpdateRequest req) {
		Room room = roomRepo.findById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found"));
		
	    if (req.state() != null) room.setState(req.state());
	    if (req.quizSetId() != null) room.setQuizSetId(req.quizSetId());
	    if (req.quizIdx() != null) room.setCurrentQuizIdx(req.quizIdx());
	    if (req.quizTimeout() != null) room.setQuizTimeout(req.quizTimeout());
		
		return roomRepo.save(room);
	}
	
	public Room createRoom(Long ownerId) {
		Room room = new Room();
		room.setCurrentQuizIdx(0);
		room.setState(RoomState.IDLE);
		room.setOwnerId(ownerId);
		
		room = roomRepo.save(room);
		addParticipant(room, ownerId);
		
		return roomRepo.save(room);
	}
	
	public void deleteRoom(Long id) {
		roomRepo.deleteById(id);
	}
	
	public ParticipantDTO getParticipant(Long userId) {
		Participant p = participantRepo.findByUserId(userId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant not found"));
		UserDTO user = userServiceClient.getUserById(userId);
		return new ParticipantDTO(user, p.getScore(), p.getAnswerIdx());
	}
	
	public Room addParticipant(Long roomId, Long userId) {
		Room room = roomRepo.findById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found"));
		addParticipant(room, userId);
		return room;
	}
	
	private void addParticipant(Room room, Long userId) {
		ParticipantId id = new ParticipantId(userId, room.getId());
		
		if (participantRepo.existsById(id))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant already exists");
		
		Participant participant = new Participant();
		participant.setId(id);
		participant.setRoom(room);
		participant.setScore(0L);
		participant.setAnswerIdx(-1);
		
		participant = participantRepo.save(participant);
		room.getParticipants().add(participant);
	}

    public void removeParticipant(Long roomId, Long userId)  {
		Room room = roomRepo.findById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found"));

        ParticipantId participantId = new ParticipantId(userId, roomId);
        participantRepo.deleteById(participantId);

        room.getParticipants().removeIf(p -> p.getId().equals(participantId));
        roomRepo.save(room);
    }
    
    public RoomDTO getRoomDTO(Room room) {
        return new RoomDTO(room, userServiceClient);
    }
}
