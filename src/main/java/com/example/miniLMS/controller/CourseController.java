package com.example.miniLMS.controller;

import com.example.miniLMS.entity.Course;
import com.example.miniLMS.entity.Module;
import com.example.miniLMS.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
@Tag(name = "Course Management", description = "APIs for managing courses and their modules")
public class CourseController {
    private final CourseService courseService;

    @Operation(
            summary = "Create a new course",
            description = "Creates a new course with the provided details. Course title is required."
    )
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        try {
            if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
                return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Course title cannot be empty"));
            }
            Course createdCourse = courseService.createCourse(course);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (Exception e) {
            log.error("Error creating course: ", e);
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get all courses",
            description = "Retrieves a list of all available courses"
    )
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            log.error("Error fetching courses: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Get course by ID",
            description = "Retrieves a specific course by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        try {
            return courseService.getCourseById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            log.error("Error fetching course by id: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Get course progress",
            description = "Calculates and returns the progress percentage for a specific user in a course"
    )
    @GetMapping("/{courseId}/progress")
    public ResponseEntity<Double> getCourseProgress(
            @PathVariable Long courseId,
            @RequestParam String userId) {
        try {
            double progress = courseService.calculateCourseProgress(courseId, userId);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            log.error("Error calculating course progress: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Add module to course",
            description = "Adds a new module to an existing course. Module title is required."
    )
    @PostMapping("/{courseId}/modules")
    public ResponseEntity<?> addModuleToCourse(
            @PathVariable Long courseId,
            @RequestBody Module module) {
        try {
            if (module.getTitle() == null || module.getTitle().trim().isEmpty()) {
                return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap("error", "Module title cannot be empty"));
            }

            Module createdModule = courseService.addModuleToCourse(courseId, module);

            // Create a clean response without circular references
            Map<String, Object> response = new HashMap<>();
            response.put("id", createdModule.getId());
            response.put("title", createdModule.getTitle());
            response.put("summary", createdModule.getSummary());
            response.put("thumbnailUrl", createdModule.getThumbnailUrl());
            response.put("coverImageUrl", createdModule.getCoverImageUrl());
            response.put("courseId", courseId);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error adding module to course: ", e);
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
