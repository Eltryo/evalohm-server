package de.thnuernberg.sep.gruppe5.be.boundary.dtos;

import jakarta.validation.constraints.NotNull;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class MessageResponseDTO {
//  @NotNull
//  private String message;
//}

public record MessageResponseDTO(@NotNull String message) {
}
