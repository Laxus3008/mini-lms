package com.example.miniLMS.service;

import com.example.miniLMS.entity.Module;
import com.example.miniLMS.entity.Lesson;
import com.example.miniLMS.entity.LessonProgress;
import com.example.miniLMS.repository.ModuleRepository;
import com.example.miniLMS.repository.LessonProgressRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final LessonProgressRepository lessonProgressRepository;

    public Optional<Module> getModuleById(Long id) {
        return moduleRepository.findById(id);
    }

    public double calculateModuleProgress(Long moduleId, String userId) {
        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new RuntimeException("Module not found"));

        List<Lesson> lessons = module.getLessons();
        if (lessons == null || lessons.isEmpty()) {
            return 0.0;
        }

        List<LessonProgress> progress = lessonProgressRepository
            .findByUserIdAndLessonIn(userId, lessons);

        long completedLessons = progress.stream()
            .filter(LessonProgress::isCompleted)
            .count();

        return (double) completedLessons / lessons.size();
    }
}
