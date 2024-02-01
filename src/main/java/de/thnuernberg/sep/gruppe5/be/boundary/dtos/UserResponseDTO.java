package de.thnuernberg.sep.gruppe5.be.boundary.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
  @NotNull
  private Integer id;

  @Pattern(regexp = "[A-Za-z0-9.]+@th-nuernberg\\.de", message = "TH-Mail")
  private String username;

  @NotBlank
  private String token;
}
