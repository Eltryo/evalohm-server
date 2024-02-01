package de.thnuernberg.sep.gruppe5.be.boundary.dtos;

import de.thnuernberg.sep.gruppe5.be.utility.validation.DatePattern;
import jakarta.validation.constraints.NotBlank;

public record AssessmentRequestDTO(@NotBlank String course, @NotBlank String lecturer,
                                   @NotBlank @DatePattern(message = "Invalid date pattern") String deadline) {
}
