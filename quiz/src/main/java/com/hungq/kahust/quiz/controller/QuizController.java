package com.hungq.kahust.quiz.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.hungq.kahust.quiz.dto.QuizSetRequest;
import com.hungq.kahust.quiz.dto.QuizAnswerResponse;
import com.hungq.kahust.quiz.dto.QuizRequest;
import com.hungq.kahust.quiz.model.Quiz;
import com.hungq.kahust.quiz.model.QuizSet;
import com.hungq.kahust.quiz.service.QuizSetService;

import jakarta.ws.rs.Path;
import lombok.AllArgsConstructor;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/quizzes")
public class QuizController {
    private QuizSetService setService;

    @GetMapping("/{quizId}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long quizId) {
        return setService.getQuizById(quizId)
        		.map(ResponseEntity::ok)
        		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/{quizId}/checkAnswer/{answerIdx}")
    public ResponseEntity<QuizAnswerResponse> checkAnswer(@PathVariable Long quizId, @PathVariable Integer answerIdx) {
    	return ResponseEntity.ok(new QuizAnswerResponse(setService.checkAnswer(quizId, answerIdx)));
    }
    
    @PutMapping("/{quizId}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long quizId, @RequestBody QuizRequest req) {
    	return ResponseEntity.ok(setService.updateQuiz(quizId, req));
    }
    
    @GetMapping("/sets")
    public ResponseEntity<List<QuizSet>> getAllSets(@RequestHeader("User-ID") Long userId) {
        List<QuizSet> sets = setService.getAllSetsByOwner(userId);
        return ResponseEntity.ok(sets);
    }
    
    @GetMapping("/sets/{setId}")
    public ResponseEntity<QuizSet> getSet(@RequestHeader("User-ID") Long userId, @PathVariable Long setId) {
        return setService.getSetById(setId)
        		.map(ResponseEntity::ok)
        		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    
    @PostMapping("/sets")
    public ResponseEntity<QuizSet> createSet(@RequestHeader("User-ID") Long userId, @RequestBody QuizSetRequest req) {
    	QuizSet set = setService.createSet(userId, req.name());
    	URI location = URI.create("/quizzes/sets/" + set.getId());
        return ResponseEntity.created(location).body(set);
    }

    @DeleteMapping("/sets/{setId}")
    public ResponseEntity<Void> deleteSet(@RequestHeader("User-ID") Long userId, @PathVariable Long setId) {
        if (!setService.isOwner(setId, userId))
        	throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        setService.deleteSet(setId);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sets/{setId}/collection")
    public ResponseEntity<QuizSet> getSet(@RequestHeader("User-ID") Long userId, @PathVariable Long setId, @RequestBody QuizRequest req) {
        if (!setService.isOwner(setId, userId))
        	throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
    	return ResponseEntity.ok(setService.addQuizToSet(setId, req));
    }
    
    @DeleteMapping("/sets/{setId}/collection/{quizId}")
    public ResponseEntity<QuizSet> removeQuizFromSet(@RequestHeader("User-ID") Long userId, @PathVariable Long setId, @PathVariable Long quizId) {
        if (!setService.isOwner(setId, userId))
        	throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        QuizSet updatedSet = setService.removeQuizFromSet(setId, quizId);
        return ResponseEntity.ok(updatedSet);
    }
}