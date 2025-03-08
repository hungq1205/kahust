package com.hungq.kahust.quiz.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hungq.kahust.quiz.dto.QuizRequest;
import com.hungq.kahust.quiz.model.ContentType;
import com.hungq.kahust.quiz.model.Quiz;
import com.hungq.kahust.quiz.model.QuizSet;
import com.hungq.kahust.quiz.repository.QuizRepository;
import com.hungq.kahust.quiz.repository.QuizSetRepository;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class QuizSetService {
    private QuizSetRepository setRepo;
    private QuizRepository quizRepo;

    public boolean isOwner(Long setId, Long userId) {
		return setRepo.findById(setId)
				.map(set -> set.getOwnerId().equals(userId))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Set not found"));
    }

    public Optional<Quiz> getQuizById(Long id) {
    	return quizRepo.findById(id);
    }
    
    public Boolean checkAnswer(Long quizId, Integer answerIdx) {
    	Quiz quiz = quizRepo.findById(quizId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
    	return quiz.getCorrectOptionIdx().equals(answerIdx);
    }
    
    public Quiz updateQuiz(Long id, QuizRequest req) {
    	Quiz quiz = quizRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
    	
        if (req.questionContent() != null) quiz.setQuestionContent(req.questionContent());
        if (req.optionContentA() != null) quiz.setOptionContentA(req.optionContentA());
        if (req.optionContentB() != null) quiz.setOptionContentB(req.optionContentB());
        if (req.optionContentC() != null) quiz.setOptionContentC(req.optionContentC());
        if (req.optionContentD() != null) quiz.setOptionContentD(req.optionContentD());
        if (req.correctOptionIdx() != null) quiz.setCorrectOptionIdx(req.correctOptionIdx());
    	
    	return quizRepo.save(quiz);
    }
    
    public Optional<QuizSet> getSetById(Long id) {
    	return setRepo.findById(id);
    }

    public List<QuizSet> getAllSetsByOwner(Long ownerId) {
        return setRepo.findByOwnerId(ownerId);
    }

    public QuizSet createSet(Long ownerId, String name) {
    	QuizSet set = new QuizSet();
        set.setOwnerId(ownerId);
        set.setName(name);
        return setRepo.save(set);
    }

    @Transactional
    public void deleteSet(Long setId) {
    	QuizSet set = setRepo.findById(setId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Set not found"));
        setRepo.delete(set);
    }

    public QuizSet addQuizToSet(Long setId, QuizRequest req) {
    	QuizSet set = setRepo.findById(setId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Set not found"));
    	Quiz quiz = new Quiz();
    	quiz.setQuestionType(ContentType.TEXT);
    	quiz.setQuestionContent(req.questionContent());
    	quiz.setOptionContentA(req.optionContentA());
    	quiz.setOptionContentB(req.optionContentB());
    	quiz.setOptionContentC(req.optionContentC());
    	quiz.setOptionContentD(req.optionContentD());
    	quiz.setCorrectOptionIdx(req.correctOptionIdx());
        quiz.setSet(set);
        
        quiz = quizRepo.save(quiz);
        set.getQuizzes().add(quiz);
        
        return setRepo.save(set);
    }

    @Transactional
    public QuizSet removeQuizFromSet(Long setId, Long quizId) {
    	QuizSet set = setRepo.findById(setId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Set not found"));
        Quiz quiz = quizRepo.findById(quizId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        if (!set.getQuizzes().contains(quiz))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz is not part of this Set");

        set.getQuizzes().remove(quiz);
        quizRepo.delete(quiz);
        
        return setRepo.save(set);
    }
}
