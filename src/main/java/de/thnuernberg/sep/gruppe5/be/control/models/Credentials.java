package de.thnuernberg.sep.gruppe5.be.control.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {
  @Pattern(regexp = "[A-Za-z0-9.]+@th-nuernberg\\.de", message = "TH-Mail")
  private String username;

  @NotBlank
  private String password;
}
