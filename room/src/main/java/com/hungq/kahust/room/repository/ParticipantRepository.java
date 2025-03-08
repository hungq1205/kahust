package com.hungq.kahust.room.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hungq.kahust.room.model.Participant;
import com.hungq.kahust.room.model.ParticipantId;

import jakarta.transaction.Transactional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {
	Optional<Participant> findById(ParticipantId id);
	
	@Query("SELECT p FROM Participant p WHERE p.id.userId = :userId")
	Optional<Participant> findByUserId(Long userId);

	@Modifying
    @Transactional
    @Query("UPDATE Participant p SET p.answerIdx = -1 WHERE p.id.roomId = :roomId")
    int resetAnswersByRoomId(Long roomId);
	
	@Modifying
    @Transactional
    @Query("UPDATE Participant p SET p.score = 0 WHERE p.id.roomId = :roomId")
    int resetScoresByRoomId(Long roomId);
}
