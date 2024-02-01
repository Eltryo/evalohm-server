package de.thnuernberg.sep.gruppe5.be.repositories;

import de.thnuernberg.sep.gruppe5.be.entity.AssessmentEntity;
import de.thnuernberg.sep.gruppe5.be.entity.SemesterEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<AssessmentEntity, Long> {
  Optional<AssessmentEntity> findAllByCourseAndSemesterAndLecturer(@NotBlank String course, @NotNull SemesterEntity semester, @NotBlank String lecturer);

  List<AssessmentEntity> findAllByCourse(String courseName);

  List<AssessmentEntity> findAllByCreator(String creatorName);

  Optional<List<AssessmentEntity>> findAllByDeadlineBeforeAndExpiredFalse(LocalDate date);

  Optional<List<AssessmentEntity>> findAllByCourseAndExpiredFalse(String course);

  Optional<AssessmentEntity> findByReviewCodeAndExpiredFalse(String reviewCode);
}
