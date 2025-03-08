package com.hungq.kahust.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hungq.kahust.quiz.model.Quiz;
import com.hungq.kahust.quiz.model.QuizSet;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSetRepository extends JpaRepository<QuizSet, Long> {
	Optional<QuizSet> findById(Long id);
    List<QuizSet> findByOwnerId(Long ownerId);
}