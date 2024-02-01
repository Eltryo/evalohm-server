package de.thnuernberg.sep.gruppe5.be.repositories;

import de.thnuernberg.sep.gruppe5.be.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<EvaluationEntity, Long> {
  List<EvaluationEntity> findAllByAssessmentId(long assessmentId);
}
