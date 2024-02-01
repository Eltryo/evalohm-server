package de.thnuernberg.sep.gruppe5.be.control.events;

import de.thnuernberg.sep.gruppe5.be.control.models.User;
import de.thnuernberg.sep.gruppe5.be.control.services.MailService;
import de.thnuernberg.sep.gruppe5.be.control.services.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationEventHandler implements ApplicationListener<OnRegistrationCompleteEvent> {
  private final VerificationService service;
  private final MailService mail;

  @Override
  public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
    this.confirmRegistration(event);
  }

  private void confirmRegistration(final OnRegistrationCompleteEvent event) {
    final User user = event.getUser();
    final String token = UUID.randomUUID().toString();
    service.createVerificationTokenForUser(user, token);

    mail.sendVerificationMail(user.getUsername(), token);
  }
}
