package com.example.miniLMS.repository;

import com.example.miniLMS.entity.Lesson;
import com.example.miniLMS.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByUserIdAndLesson(String userId, Lesson lesson);
    List<LessonProgress> findByUserIdAndLessonIn(String userId, List<Lesson> lessons);
}
