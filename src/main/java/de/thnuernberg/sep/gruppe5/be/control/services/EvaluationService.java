package de.thnuernberg.sep.gruppe5.be.control.services;

import de.thnuernberg.sep.gruppe5.be.control.mapper.EvaluationMapper;
import de.thnuernberg.sep.gruppe5.be.control.models.Evaluation;
import de.thnuernberg.sep.gruppe5.be.entity.AssessmentEntity;
import de.thnuernberg.sep.gruppe5.be.entity.EvaluationEntity;
import de.thnuernberg.sep.gruppe5.be.entity.SubmittedAssessmentEntity;
import de.thnuernberg.sep.gruppe5.be.entity.UserEntity;
import de.thnuernberg.sep.gruppe5.be.repositories.AssessmentRepository;
import de.thnuernberg.sep.gruppe5.be.repositories.EvaluationRepository;
import de.thnuernberg.sep.gruppe5.be.repositories.SubmittedAssessmentRepository;
import de.thnuernberg.sep.gruppe5.be.repositories.UserRepository;
import de.thnuernberg.sep.gruppe5.be.utility.exceptions.AppException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvaluationService {
  private final EvaluationRepository evaluationRepository;
  private final EvaluationMapper evaluationMapper;
  private final SubmittedAssessmentRepository submittedAssessmentRepository;
  private final UserRepository userRepository;
  private final AssessmentRepository assessmentRepository;

  public void addEvaluation(@Valid Evaluation evaluation) {
    UserEntity loggedInUser = this.userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
      .orElseThrow(() -> new AppException("Der angemeldete Benutzer konnte nicht gefunden werden", HttpStatus.INTERNAL_SERVER_ERROR));

    AssessmentEntity evaluatedAssessment = this.assessmentRepository.findById(evaluation.getAssessmentId())
      .orElseThrow(() -> new AppException("Die bewertete Lehrveranstaltung konnte nicht gefunden werden", HttpStatus.NOT_FOUND));

    if (evaluatedAssessment.isExpired() || evaluatedAssessment.isClosed())
      throw new AppException("Die Bewertungsphase für diese Lehrveranstaltung ist leider schon beendet", HttpStatus.BAD_REQUEST);

    if (!evaluatedAssessment.getReviewCode().equals(evaluation.getReviewCode()))
      throw new AppException("Der Reviewcode stimmt nicht!", HttpStatus.BAD_REQUEST);

    Optional<List<SubmittedAssessmentEntity>> submittedAssessments = this.submittedAssessmentRepository.findAllByUserId(loggedInUser.getId());
    if (submittedAssessments.isPresent()) {
      for (SubmittedAssessmentEntity submittedAssessment : submittedAssessments.get()) {
        if (evaluation.getAssessmentId().longValue() == submittedAssessment.getAssessmentId().longValue()) {
          throw new AppException("Diese Lehrveranstaltung wurde bereits bewertet!", HttpStatus.BAD_REQUEST);
        }
      }
    }

    SubmittedAssessmentEntity submittedAssessment = new SubmittedAssessmentEntity();
    submittedAssessment.setAssessmentId(evaluatedAssessment.getId());
    submittedAssessment.setUserId(loggedInUser.getId());

    this.submittedAssessmentRepository.save(submittedAssessment);
    this.evaluationRepository.save(this.evaluationMapper.toEvaluationEntity(evaluation));
  }

  public List<Evaluation> getEvaluationsFromAssessment(long assessmentId) {
    AssessmentEntity evaluatedAssessment = this.assessmentRepository.findById(assessmentId)
      .orElseThrow(() -> new AppException("Die bewertete Lehrveranstaltung konnte nicht gefunden werden", HttpStatus.NOT_FOUND));

    if (!(evaluatedAssessment.isExpired() || evaluatedAssessment.isClosed()))
      throw new AppException("Die Bewertungsphase für diese Lehrveranstaltung ist noch nicht beendet", HttpStatus.BAD_REQUEST);

    List<EvaluationEntity> evaluations = evaluationRepository.findAllByAssessmentId(assessmentId);

    return evaluations.stream().map(evaluationMapper::toEvaluation).toList();
  }

  public float getGradeForCourse(String courseName) {
    List<AssessmentEntity> assessmentsForCourse = this.assessmentRepository.findAllByCourse(courseName);
    if (assessmentsForCourse.isEmpty())
      throw new AppException("Zu diesem Kurs konnten keine Bewertungen gefunden werden", HttpStatus.NOT_FOUND);

    List<Float> gradesForAssessments = new ArrayList<>();
    for (int i = 0; i < assessmentsForCourse.toArray().length; i++) {
      if (!(assessmentsForCourse.get(i).isClosed() || assessmentsForCourse.get(i).isExpired()))
        break;

      List<EvaluationEntity> evaluationsForAssessment = this.evaluationRepository.findAllByAssessmentId(assessmentsForCourse.get(i).getId());
      float[] averageValues = calculateAverageValues(evaluationsForAssessment);
      float grade = calculateGrade(averageValues);
      if (grade != 6) {
        gradesForAssessments.add(grade);
      }
    }

    if (gradesForAssessments.isEmpty()) return 6;

    int gradesAmount = 0;
    float gradeSum = 0;
    for (Float grade : gradesForAssessments) {
      gradeSum += grade;
      gradesAmount++;
    }

    return Math.round((gradeSum / gradesAmount) * 100) / 100.0f;
  }

  private float calculateGrade(float[] averageValues) {
    if (averageValues[0] == 0) {
      return 6;
    }

    float score = 0;
    float gradedAspectsAmount = 0;
    for (int i = 0; i < averageValues.length; i++) {
      float value = averageValues[i];
      if (value == 0) break;

      if (i == 4 || i == 6 || i == 7) {
        int averageValue = Math.round(value);
        if (averageValue == 1 || averageValue == 5) {
          score += 1;
        } else if (averageValue == 2 || averageValue == 4) {
          score += 3;
        } else {
          score += 5;
        }
      } else {
        score += value;
      }

      gradedAspectsAmount++;
    }

    return 6 - Math.round((score / gradedAspectsAmount) * 100) / 100.0f;
  }

  private float[] calculateAverageValues(List<EvaluationEntity> evaluationsForAssessment) {
    if (evaluationsForAssessment.isEmpty()) {
      return new float[9];
    }

    int[] sumOfValues = new int[9];
    int[] counter = new int[9];
    for (EvaluationEntity evaluation : evaluationsForAssessment) {
      if (evaluation.getVorlesungsRating() != 0) {
        sumOfValues[0] += evaluation.getVorlesungsRating();
        counter[0]++;
      }

      if (evaluation.getUebungsRating() != 0) {
        sumOfValues[1] += evaluation.getUebungsRating();
        counter[1]++;
      }

      if (evaluation.getUnterlagenRating() != 0) {
        sumOfValues[2] += evaluation.getUnterlagenRating();
        counter[2]++;
      }

      if (evaluation.getPruefungsRating() != 0) {
        sumOfValues[3] += evaluation.getPruefungsRating();
        counter[3]++;
      }

      if (evaluation.getZeitaufwandRating() != 0) {
        sumOfValues[4] += evaluation.getZeitaufwandRating();
        counter[4]++;
      }

      if (evaluation.getInhaltRating() != 0) {
        sumOfValues[5] += evaluation.getInhaltRating();
        counter[5]++;
      }

      if (evaluation.getStoffmengeRating() != 0) {
        sumOfValues[6] += evaluation.getStoffmengeRating();
        counter[6]++;
      }

      if (evaluation.getNiveauRating() != 0) {
        sumOfValues[7] += evaluation.getNiveauRating();
        counter[7]++;
      }

      if (evaluation.getRelevanzRating() != 0) {
        sumOfValues[8] += evaluation.getRelevanzRating();
        counter[8]++;
      }
    }

    float[] averageValues = new float[9];
    averageValues[0] = Math.round(((float) sumOfValues[0] / counter[0]) * 100) / 100.0f;
    averageValues[1] = Math.round(((float) sumOfValues[1] / counter[1]) * 100) / 100.0f;
    averageValues[2] = Math.round(((float) sumOfValues[2] / counter[2]) * 100) / 100.0f;
    averageValues[3] = Math.round(((float) sumOfValues[3] / counter[3]) * 100) / 100.0f;
    averageValues[4] = Math.round(((float) sumOfValues[4] / counter[4]) * 100) / 100.0f;
    averageValues[5] = Math.round(((float) sumOfValues[5] / counter[5]) * 100) / 100.0f;
    averageValues[6] = Math.round(((float) sumOfValues[6] / counter[6]) * 100) / 100.0f;
    averageValues[7] = Math.round(((float) sumOfValues[7] / counter[7]) * 100) / 100.0f;
    averageValues[8] = Math.round(((float) sumOfValues[8] / counter[8]) * 100) / 100.0f;

    return averageValues;
  }
}
