package com.hungq.kahust.room.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hungq.kahust.room.dto.ExistsRoomOfUserResponse;
import com.hungq.kahust.room.dto.ParticipantAnswerRequest;
import com.hungq.kahust.room.dto.ParticipantDTO;
import com.hungq.kahust.room.dto.ParticipantRequest;
import com.hungq.kahust.room.dto.QuizAnswerResponse;
import com.hungq.kahust.room.dto.RoomDTO;
import com.hungq.kahust.room.dto.RoomUpdateRequest;
import com.hungq.kahust.room.dto.ScoreUpdateRequest;
import com.hungq.kahust.room.model.Room;
import com.hungq.kahust.room.service.RoomService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/rooms")
public class RoomController {
	private final RoomService roomService;

	@GetMapping("/ofUser/{userId}")
	public ResponseEntity<RoomDTO> getRoomOfUser(@PathVariable Long userId) {
		return roomService.findRoomOfUser(userId)
				.map(roomService::getRoomDTO)
				.map(ResponseEntity::ok)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
	}
	
	@GetMapping("/ofUser/{userId}/exists")
	public ResponseEntity<ExistsRoomOfUserResponse> existsRoomOfUser(@PathVariable Long userId) {
		return ResponseEntity.ok(
				new ExistsRoomOfUserResponse(
						roomService.findRoomOfUser(userId).isPresent()));
	}

	@GetMapping("/participants")
	public ResponseEntity<ParticipantDTO> updateParticipantAnswer(@RequestHeader("User-ID") Long userId) {		
		return ResponseEntity.ok(roomService.getParticipant(userId));
	}
	
	@GetMapping("")
	public ResponseEntity<List<RoomDTO>> getRooms() {
		return ResponseEntity.ok(roomService.findRooms().stream().map(roomService::getRoomDTO).toList());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
		return roomService.findRoomById(id)
			.map(roomService::getRoomDTO)
			.map(ResponseEntity::ok)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
	}
	
	@PutMapping("/{roomId}")
	public ResponseEntity<RoomDTO> updateRoom(@RequestHeader("User-ID") Long userId, @PathVariable Long roomId, @RequestBody RoomUpdateRequest req) {
		if (!roomService.isOwner(roomId, userId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		return ResponseEntity.ok(roomService.getRoomDTO(roomService.updateRoom(roomId, req)));
	}
	
	@PostMapping("")
	public ResponseEntity<RoomDTO> createRoom(@RequestHeader("User-ID") Long userId, @RequestBody RoomUpdateRequest req) {
		Room room = roomService.createRoom(userId);
		room = roomService.updateRoom(room.getId(), req);
		URI location = URI.create("/rooms/" + room.getId()); 
		return ResponseEntity.created(location).body(roomService.getRoomDTO(room));
	}
	
	@DeleteMapping("/{roomId}")
	public ResponseEntity<Void> deleteRoom(@RequestHeader("User-ID") Long userId, @PathVariable Long roomId) {
		if (!roomService.isOwner(roomId, userId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		
	    roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
	}

	@PostMapping("/{roomId}/participants/answer")
	public ResponseEntity<QuizAnswerResponse> updateParticipantAnswer(@RequestHeader("User-ID") Long userId, @PathVariable Long roomId, @RequestBody ParticipantAnswerRequest req) {		
		return ResponseEntity.ok(roomService.updateParticipantAnswer(userId, req.quizId(), req.answerIdx()));
	}
	
	@PostMapping("/{roomId}/participants/resetAnswers")
	public ResponseEntity<Void> resetAnswers(@RequestHeader("User-ID") Long userId, @PathVariable Long roomId) {	
		if (!roomService.isOwner(roomId, userId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		
		roomService.resetAnswersInRoom(roomId);
        return ResponseEntity.ok().build();
	}

	@PostMapping("/{roomId}/participants/score")
	public ResponseEntity<Void> updateParticipantAnswer(@RequestHeader("User-ID") Long userId, @PathVariable Long roomId, @RequestBody ScoreUpdateRequest req) {
		roomService.updateAddParticipantScore(userId, req.score());
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/{roomId}/participants/resetScores")
	public ResponseEntity<Void> resetScores(@RequestHeader("User-ID") Long userId, @PathVariable Long roomId) {	
		if (!roomService.isOwner(roomId, userId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		
		roomService.resetScoresInRoom(roomId);
        return ResponseEntity.ok().build();
	}
	
	@PostMapping("/{roomId}/participants")
	public ResponseEntity<RoomDTO> addParticipant(@RequestHeader("User-ID") Long userId, @PathVariable Long roomId) {
		Room room = roomService.addParticipant(roomId, userId);
		URI location = URI.create("/rooms/" + room.getId()); 
		return ResponseEntity.created(location).body(roomService.getRoomDTO(room));
	}
	
	@DeleteMapping("/{roomId}/participants")
	public ResponseEntity<RoomDTO> removeParticipant(@RequestHeader("User-ID") Long userId, @RequestBody ParticipantRequest req, @PathVariable Long roomId) {
		if (!roomService.isOwner(roomId, userId)) {
			if (userId != req.userId())
				throw new ResponseStatusException(HttpStatus.FORBIDDEN);			
		}
		else if (userId == req.userId()) {
		    roomService.deleteRoom(roomId);
		    return ResponseEntity.ok(null);
		}
		
		roomService.removeParticipant(roomId, req.userId());
		Room room = roomService.findRoomById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
		return ResponseEntity.ok(roomService.getRoomDTO(room));
	}
}
