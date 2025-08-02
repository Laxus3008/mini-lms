package com.example.miniLMS.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    private boolean completed;
}
