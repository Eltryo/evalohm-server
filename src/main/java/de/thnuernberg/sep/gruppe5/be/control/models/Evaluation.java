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
  @Min(1)
  @Max(5)
  int lectureRating;
  @Min(1)
  @Max(5)
  int exerciseRating;
  @Min(1)
  @Max(5)
  int papersRating;
  @Min(1)
  @Max(5)
  int examRating;
  @Min(0)
  @Max(5)
  int timeExpenditureRating;
  @Min(0)
  @Max(5)
  int contentRating;
  @Min(0)
  @Max(5)
  int scopeRating;
  @Min(0)
  @Max(5)
  int difficultyRating;
  @Min(0)
  @Max(5)
  int relevanceRating;
  @NotNull
  private Long assessmentId;
  @NotBlank
  private String reviewCode;
  private String remark;
}
