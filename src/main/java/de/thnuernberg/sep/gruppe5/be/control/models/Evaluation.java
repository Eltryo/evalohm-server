package de.thnuernberg.sep.gruppe5.be.control.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Evaluation {
  @NotNull
  private Long assessmentId;

  @NotBlank
  private String reviewCode;

  @Min(1)
  @Max(5)
  private int vorlesungsRating;

  @Min(1)
  @Max(5)
  private int uebungsRating;

  @Min(1)
  @Max(5)
  private int unterlagenRating;

  @Min(1)
  @Max(5)
  private int pruefungsRating;

  @Min(0)
  @Max(5)
  private int zeitaufwandRating;

  @Min(0)
  @Max(5)
  private int inhaltRating;

  @Min(0)
  @Max(5)
  private int stoffmengeRating;

  @Min(0)
  @Max(5)
  private int niveauRating;

  @Min(0)
  @Max(5)
  private int relevanzRating;

  private String bemerkung;
}
