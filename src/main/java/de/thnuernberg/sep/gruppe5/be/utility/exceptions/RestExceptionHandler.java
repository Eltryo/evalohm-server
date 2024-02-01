package de.thnuernberg.sep.gruppe5.be.utility.exceptions;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {
  @ExceptionHandler(value = {AppException.class})
  public ResponseEntity<ErrorResponseDTO> handleAppException(AppException ex) {
    return ResponseEntity.status(ex.getStatus())
      .body(new ErrorResponseDTO(ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
    List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();

    if (errorMessages.contains("TH-Mail")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDTO("Sie müssen eine TH-Email-Adresse verwenden."));
    }

    if (errorMessages.contains("Invalid date pattern")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDTO("Das angegebene Datenformat ist nicht korrekt"));
    }

    //todo: muesste es nicht ein bad request zurueckgeben
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(new ErrorResponseDTO("Es scheint etwas schiefgelaufen zu sein."));
  }
}
