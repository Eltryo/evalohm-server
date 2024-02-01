package de.thnuernberg.sep.gruppe5.be.control.models;

import de.thnuernberg.sep.gruppe5.be.utility.validation.DatePattern;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AssessmentModel {
  private Long id;
  @NotBlank
  private String course;
  @NotBlank
  private String lecturer;
  @DatePattern
  private LocalDate deadline;
  @DatePattern
  private LocalDate creationDate;
  @Valid
  private SemesterModel semester;
  private boolean expired;
  private String creator;
  private String reviewCode;
  private boolean closed;
}
