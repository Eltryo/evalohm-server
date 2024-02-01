package de.thnuernberg.sep.gruppe5.be.repositories;

import de.thnuernberg.sep.gruppe5.be.entity.SubmittedAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubmittedAssessmentRepository extends JpaRepository<SubmittedAssessmentEntity, Long> {
  Optional<List<SubmittedAssessmentEntity>> findAllByUserId(Integer userId);

  @Query("select userId from SubmittedAssessmentEntity where assessmentId = ?1")
  Optional<List<Integer>> findUserIdsByAssessmentId(Long assessmentId);

  @Query("select assessmentId from SubmittedAssessmentEntity where userId = ?1")
  List<Long> findAssessmentIdsByUserIds(Integer userId);
}
