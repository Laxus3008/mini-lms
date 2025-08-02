package com.example.miniLMS.service;

import com.example.miniLMS.entity.Lesson;
import com.example.miniLMS.entity.LessonProgress;
import com.example.miniLMS.entity.Module;
import com.example.miniLMS.entity.LessonType;
import com.example.miniLMS.repository.LessonRepository;
import com.example.miniLMS.repository.LessonProgressRepository;
import com.example.miniLMS.repository.ModuleRepository;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final LessonProgressRepository progressRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public Lesson createLesson(Long moduleId, Lesson lesson) {

        Module found = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        validateLessonContent(lesson);
        lesson.setModule(found);
        found.getLessons().add(lesson);

        // Save only through the module to avoid duplicate saves
        moduleRepository.save(found);
        return lesson;
    }

    public Optional<Lesson> getLessonById(Long id) {
        return lessonRepository.findById(id);
    }

    public LessonProgress markLessonComplete(Long lessonId, String userId) {
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new RuntimeException("Lesson not found"));

        LessonProgress progress = progressRepository
            .findByUserIdAndLesson(userId, lesson)
            .orElse(new LessonProgress());

        progress.setUserId(userId);
        progress.setLesson(lesson);
        progress.setCompleted(true);

        return progressRepository.save(progress);
    }

    public Object getLessonContent(Long lessonId) throws Exception {
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // For TEXT type, return the content directly from database
        if (lesson.getType() == LessonType.TEXT) {
            return lesson.getContent();
        }

        // For file types (VIDEO, IMAGE, PDF), return the file as a Resource
        return fileStorageService.loadFileAsResource(lesson.getContent());
    }

    private void validateLessonContent(Lesson lesson) {
        if (lesson.getContent() == null || lesson.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Lesson content cannot be empty");
        }

        if (lesson.getType() == null) {
            throw new IllegalArgumentException("Lesson type cannot be null");
        }

        // For TEXT type, just check if content exists
        if (lesson.getType() == LessonType.TEXT) {
            return;
        }

        // For uploaded files, verify file exists in upload directory
        if (lesson.getType() == LessonType.VIDEO ||
            lesson.getType() == LessonType.IMAGE ||
            lesson.getType() == LessonType.PDF) {
            return;
        }
    }
}
