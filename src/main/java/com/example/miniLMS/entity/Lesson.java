package com.example.miniLMS.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private LessonType type;

    @Column(columnDefinition = "TEXT")
    private String content;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;
}
