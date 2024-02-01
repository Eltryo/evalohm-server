package de.thnuernberg.sep.gruppe5.be.boundary.controller;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.EvaluationDTO;
import de.thnuernberg.sep.gruppe5.be.boundary.dtos.MessageResponseDTO;
import de.thnuernberg.sep.gruppe5.be.boundary.mapper.EvaluationDTOMapper;
import de.thnuernberg.sep.gruppe5.be.control.services.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EvaluationController {
  private final EvaluationService evaluationService;
  private final EvaluationDTOMapper evaluationDTOMapper;

  @PreAuthorize("hasRole('STUDENT')")
  @PostMapping("/evaluation")
  public ResponseEntity<MessageResponseDTO> addEvaluation(@Valid @RequestBody EvaluationDTO evaluation) {
    this.evaluationService.addEvaluation(this.evaluationDTOMapper.toEvaluation(evaluation));
    return ResponseEntity.ok(new MessageResponseDTO("Daten erfolgreich empfangen"));
  }

}

