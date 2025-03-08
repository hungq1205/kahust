package com.hungq.kahust.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hungq.kahust.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findById(Long id);
	Optional<User> findByUsername(String username);
	List<User> findAll();
}
