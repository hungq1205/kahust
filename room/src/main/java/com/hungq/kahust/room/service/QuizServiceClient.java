package com.hungq.kahust.room.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hungq.kahust.room.dto.QuizAnswerResponse;

@Service
public class QuizServiceClient {
	private final WebClient client;
	private final String quizServiceUrl = "http://quiz-service:3001";
	
	public QuizServiceClient(WebClient.Builder builder) {
		this.client = builder.baseUrl(quizServiceUrl).build();
	}

    public QuizAnswerResponse checkAnswer(Long quizId, Integer answerIdx) {
        return client.get()
            .uri("/quizzes/" + quizId + "/checkAnswer/" + answerIdx)
            .retrieve()
            .bodyToMono(QuizAnswerResponse.class)
            .block();
    }
}
