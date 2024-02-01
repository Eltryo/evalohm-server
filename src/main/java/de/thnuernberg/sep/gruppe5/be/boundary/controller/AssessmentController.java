package de.thnuernberg.sep.gruppe5.be.boundary.controller;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.*;
import de.thnuernberg.sep.gruppe5.be.boundary.mapper.AssessmentDTOMapper;
import de.thnuernberg.sep.gruppe5.be.boundary.mapper.EvaluationDTOMapper;
import de.thnuernberg.sep.gruppe5.be.control.models.Assessment;
import de.thnuernberg.sep.gruppe5.be.control.models.Evaluation;
import de.thnuernberg.sep.gruppe5.be.control.services.AssessmentService;
import de.thnuernberg.sep.gruppe5.be.control.services.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/assessments")
@RequiredArgsConstructor
public class AssessmentController {
  private final AssessmentDTOMapper assessmentMapper;
  private final AssessmentService assessmentService;
  private final EvaluationDTOMapper evaluationMapper;
  private final EvaluationService evaluationService;

  @PreAuthorize("hasRole('PROF')")
  @PostMapping
  public AssessmentResponseDTO addAssessment(@Valid @RequestBody AssessmentRequestDTO assessmentRequestDTO) {
    Assessment assessmentModel = assessmentMapper.toAssessmentModel(assessmentRequestDTO);
    Assessment result = assessmentService.addAssessment(assessmentModel);
    return assessmentMapper.toAssessmentDTO(result);
  }

  @PreAuthorize("hasRole('PROF')")
  @GetMapping("/myAssessments")
  public List<AssessmentResponseDTO> getCreatedAssessmentsFromLoggedInUser() {
    List<Assessment> result = assessmentService.getCreatedAssessmentsFromLoggedInUser();
    return result.stream().map(assessmentMapper::toAssessmentDTO).toList();
  }

  @GetMapping("/{assessmentId}")
  public AssessmentResponseDTO getAssessmentById(@PathVariable Long assessmentId) {
    return assessmentMapper.toAssessmentDTO(assessmentService.getAssessmentById(assessmentId));
  }

  @PreAuthorize("hasRole('PROF')")
  @PutMapping("/{assessmentId}")
  public AssessmentResponseDTO updateAssessment(@Valid @RequestBody AssessmentResponseDTO assessment) {
    return assessmentMapper.toAssessmentDTO(assessmentService.updateAssessment(assessmentMapper.toAssessmentModel(assessment)));
  }

  @GetMapping
  public List<AssessmentResponseDTO> getAllAssessments() {
    List<Assessment> result = assessmentService.getAllAssessments();
    return result.stream().map(assessmentMapper::toAssessmentDTO).toList();
  }

  @PreAuthorize("hasRole('PROF')")
  @PutMapping("/{assessmentId}/close")
  public MessageResponseDTO closeAssessment(@PathVariable Long assessmentId) {
    assessmentService.closeAssessment(assessmentId);
    return new MessageResponseDTO("Bewertung wurde geschlossen");
  }

  @GetMapping("/{assessmentId}/results")
  public List<EvaluationDTO> getAssessmentResults(@PathVariable long assessmentId) {
    List<Evaluation> result = evaluationService.getEvaluationsFromAssessment(assessmentId);
    return result.stream().map(evaluationMapper::toEvaluationDTO).toList();
  }

  @GetMapping("/mySubmittedAssessments")
  public List<AssessmentIdResponseDTO> getSubmittedAssessmentsFromLoggedInUser() {
    List<Long> result = assessmentService.getSubmittedAssessmentsFromLoggedInUser();
    return result.stream().map(AssessmentIdResponseDTO::new).toList();
  }

  @PutMapping("/grade")
  public float getAssessmentResults(@Valid @RequestBody CourseResponseDTO course) {
    return evaluationService.getGradeForCourse(course.courseName());
  }
}
