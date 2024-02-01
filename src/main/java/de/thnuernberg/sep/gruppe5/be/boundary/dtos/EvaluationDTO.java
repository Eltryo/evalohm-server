package de.thnuernberg.sep.gruppe5.be.boundary.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EvaluationDTO(
  @NotNull Long assessmentId,
  @NotBlank String reviewCode,
  @Min(1) @Max(5) int vorlesungsRating,
  @Min(1) @Max(5) int uebungsRating,
  @Min(1) @Max(5) int unterlagenRating,
  @Min(1) @Max(5) int pruefungsRating,
  @Min(0) @Max(5) int zeitaufwandRating,
  @Min(0) @Max(5) int inhaltRating,
  @Min(0) @Max(5) int stoffmengeRating,
  @Min(0) @Max(5) int niveauRating,
  @Min(0) @Max(5) int relevanzRating,
  String bemerkung
) {
}
