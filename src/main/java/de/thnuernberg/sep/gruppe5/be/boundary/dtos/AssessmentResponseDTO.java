package de.thnuernberg.sep.gruppe5.be.boundary.dtos;

import java.time.LocalDate;

public record AssessmentResponseDTO(Long id, String course, SemesterDTO semester, String lecturer,
                                    LocalDate creationDate, LocalDate deadline, boolean expired, String creator,
                                    String reviewCode, boolean closed) {
}
