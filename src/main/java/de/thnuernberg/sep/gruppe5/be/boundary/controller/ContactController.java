package de.thnuernberg.sep.gruppe5.be.boundary.controller;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.ContactRequestDTO;
import de.thnuernberg.sep.gruppe5.be.boundary.dtos.MessageResponseDTO;
import de.thnuernberg.sep.gruppe5.be.control.services.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {
  private final MailService mailService;

  @PostMapping
  public ResponseEntity<MessageResponseDTO> contact(@Valid @RequestBody ContactRequestDTO contact) {
    mailService.sendContactMail(contact);
    return ResponseEntity.ok(new MessageResponseDTO("Wir haben Ihre Nachricht erhalten und werden uns umgehend um Ihr Anliegen kümmern."));
  }
}
