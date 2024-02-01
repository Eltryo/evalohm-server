package de.thnuernberg.sep.gruppe5.be.boundary.dtos;

import jakarta.validation.constraints.NotBlank;

public record VerificationTokenRequestDTO(@NotBlank String token) {
}
