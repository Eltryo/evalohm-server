package de.thnuernberg.sep.gruppe5.be.control.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Passwords {
  @NotBlank
  private String oldPassword;

  @NotBlank
  private String newPassword;
}
