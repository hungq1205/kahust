package com.hungq.kahust.room.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hungq.kahust.room.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>{
	public Optional<Room> findById(Long id);
}
