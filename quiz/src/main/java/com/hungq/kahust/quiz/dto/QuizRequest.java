package com.hungq.kahust.quiz.dto;

public record QuizRequest(
	String questionContent, 
	String optionContentA, 
	String optionContentB, 
	String optionContentC, 
	String optionContentD, 
	Integer correctOptionIdx
) {}
