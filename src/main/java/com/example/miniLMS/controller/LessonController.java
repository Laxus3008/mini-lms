package com.example.miniLMS.controller;

import com.example.miniLMS.entity.Lesson;
import com.example.miniLMS.entity.LessonProgress;
import com.example.miniLMS.entity.LessonType;
import com.example.miniLMS.service.LessonService;
import com.example.miniLMS.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@RestController
@RequestMapping("/lessons")
@CrossOrigin
@Tag(name = "Lesson Management", description = "APIs for managing lessons")
@Slf4j
public class LessonController {
    @Autowired
    private LessonService lessonService;

    @Autowired
    private FileStorageService fileStorageService;

    @Operation(
            summary = "Create a new lesson",
            description = "Creates a new lesson within a specified module. Module ID is required."
    )
    @PostMapping(
        path = "/modules/{moduleId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> createLesson(
            @PathVariable Long moduleId,
            @RequestParam String title,
            @RequestParam LessonType type,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile file) {
        try {
            log.info("Creating lesson - Title: {}, Type: {}, ModuleId: {}", title, type, moduleId);

            Lesson lesson = new Lesson();
            lesson.setTitle(title);
            lesson.setType(type);

            if (type == LessonType.TEXT) {
                if (content == null || content.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Text content is required for TEXT type lessons");
                }
                lesson.setContent(content);
            } else {
                if (file == null || file.isEmpty()) {
                    return ResponseEntity.badRequest().body("File is required for " + type + " type lessons");
                }
                log.info("Received file: {}, size: {}", file.getOriginalFilename(), file.getSize());
                String fileName = fileStorageService.storeFile(file);
                lesson.setContent(fileName);
            }

            Lesson savedLesson = lessonService.createLesson(moduleId, lesson);
            log.info("Lesson created successfully with ID: {}", savedLesson.getId());
            return ResponseEntity.ok(savedLesson);

        } catch (Exception e) {
            log.error("Error creating lesson", e);
            return ResponseEntity.internalServerError()
                .body("Error creating lesson: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Get all lessons in a module",
            description = "Retrieves all lessons for a specified module"
    )
    @GetMapping("/{lessonId}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Long lessonId) {
        return lessonService.getLessonById(lessonId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Mark lesson as complete",
            description = "Marks a lesson as complete for a specific user"
    )
    @PostMapping("/{lessonId}/progress")
    public ResponseEntity<LessonProgress> markLessonComplete(
            @PathVariable Long lessonId,
            @RequestParam String userId) {
        return ResponseEntity.ok(lessonService.markLessonComplete(lessonId, userId));
    }

    @Operation(
            summary = "Fetches the content of the lesson",
            description = "this end point is for fetching the content of the lesson based on its type"
    )
    @GetMapping("/{lessonId}/content")
    public ResponseEntity<?> getLessonContent(@PathVariable Long lessonId) {
        try {
            Object content = lessonService.getLessonContent(lessonId);

            // If content is a String, it's TEXT type
            if (content instanceof String) {
                return ResponseEntity.ok(content);
            }

            // If content is a Resource, it's a file (VIDEO, IMAGE, PDF)
            if (content instanceof Resource) {
                Resource resource = (Resource) content;
                Lesson lesson = lessonService.getLessonById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));

                String contentType = switch (lesson.getType()) {
                    case VIDEO -> "video/mp4";
                    case IMAGE -> "image/jpeg";
                    case PDF -> "application/pdf";
                    default -> "application/octet-stream";
                };

                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            }

            return ResponseEntity.internalServerError()
                .body("Unexpected content type returned");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error retrieving content: " + e.getMessage());
        }
    }
}
