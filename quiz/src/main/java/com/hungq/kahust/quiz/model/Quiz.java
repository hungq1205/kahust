package com.hungq.kahust.quiz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContentType questionType = ContentType.TEXT;
    
    @Column(nullable = false)
    private String questionContent;
    
    @Column(nullable = false)
    private Integer correctOptionIdx = 0;

    private String optionContentA, optionContentB, optionContentC, optionContentD; 
    
    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private QuizSet set;
}
