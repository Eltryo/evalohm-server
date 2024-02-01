package de.thnuernberg.sep.gruppe5.be.control.services;

import de.thnuernberg.sep.gruppe5.be.entity.AssessmentEntity;
import de.thnuernberg.sep.gruppe5.be.repositories.AssessmentRepository;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Aspect
@Service
public class AssessmentExpiryCheckService {

  private final AssessmentRepository assessmentRepository;
  private final AssessmentService assessmentService;

  public AssessmentExpiryCheckService(AssessmentRepository assessmentRepository, AssessmentService assessmentService) {
    this.assessmentRepository = assessmentRepository;
    this.assessmentService = assessmentService;
  }

  @Before("execution(* de.thnuernberg.sep.gruppe5.be.boundary.controller.AssessmentController.*(..))")
  public void checkAndMarkExpiredAssessments() {
    LocalDate currentDate = LocalDate.now();

    Optional<List<AssessmentEntity>> assessmentsToMarkAsExpired = assessmentRepository.findAllByDeadlineBeforeAndExpiredFalse(currentDate);
    if (assessmentsToMarkAsExpired.isPresent()) {
      for (AssessmentEntity assessmentToMarkAsExpired : assessmentsToMarkAsExpired.get()) {
        assessmentToMarkAsExpired.setExpired(true);
        AssessmentEntity expiredAssessment = assessmentRepository.save(assessmentToMarkAsExpired);
        assessmentService.notifyEvaluators(expiredAssessment);
      }
    }
  }

}
