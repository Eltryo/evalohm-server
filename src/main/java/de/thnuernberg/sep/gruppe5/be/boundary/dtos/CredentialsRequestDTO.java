package de.thnuernberg.sep.gruppe5.be.boundary.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CredentialsRequestDTO(
  @Pattern(regexp = "[A-Za-z0-9.]+@th-nuernberg\\.de", message = "TH-Mail") String username,
  @NotBlank String password) {
}
