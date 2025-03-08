package com.hungq.kahust.quiz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hungq.kahust.quiz.model.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
	Optional<Quiz> findById(Long id);
}
