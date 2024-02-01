package de.thnuernberg.sep.gruppe5.be.control.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssessmentMail {
  @NotBlank
  private String course;

  @NotBlank
  private String lecturer;

  private Semester semester;
}
