package de.thnuernberg.sep.gruppe5.be.boundary.dtos;

import jakarta.validation.constraints.NotBlank;

public record ContactRequestDTO(@NotBlank String subject, @NotBlank String text, @NotBlank String contactData) {
}
