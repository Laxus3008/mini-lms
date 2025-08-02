package com.example.miniLMS.service;

import com.example.miniLMS.entity.Course;
import com.example.miniLMS.entity.Module;
import com.example.miniLMS.repository.CourseRepository;
import com.example.miniLMS.repository.ModuleRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleService moduleService;

    public Course createCourse(Course course) {
        log.info("Creating course with title: {}", course.getTitle());
        try {
            if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Course title cannot be empty");
            }

            if (course.getModules() == null) {
                course.setModules(new ArrayList<>());
            }

            Course savedCourse = courseRepository.save(course);
            log.info("Successfully created course with id: {}", savedCourse.getId());
            return savedCourse;
        } catch (Exception e) {
            log.error("Error creating course: {}", e.getMessage());
            throw e;
        }
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public double calculateCourseProgress(Long courseId, String userId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));

        if (course.getModules() == null || course.getModules().isEmpty()) {
            return 0.0;
        }

        return course.getModules().stream()
            .mapToDouble(module -> moduleService.calculateModuleProgress(module.getId(), userId))
            .average()
            .orElse(0.0);
    }

    @Transactional
    public Module addModuleToCourse(Long courseId, Module module) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));

        if (module.getLessons() == null) {
            module.setLessons(new ArrayList<>());
        }

        module.setCourse(course);
        course.getModules().add(module);
        courseRepository.save(course);
        return moduleRepository.save(module);
    }
}
