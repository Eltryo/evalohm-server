package de.thnuernberg.sep.gruppe5.be.boundary.dtos;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record AssessmentResponseDTO(Long id, @NotBlank String course, SemesterDTO semester, @NotBlank String lecturer,
                                    LocalDate creationDate,
                                    LocalDate deadline, boolean expired, String creator, String reviewCode,
                                    boolean closed) {
}
