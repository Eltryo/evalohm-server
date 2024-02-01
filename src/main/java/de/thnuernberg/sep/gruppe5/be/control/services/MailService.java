package de.thnuernberg.sep.gruppe5.be.control.services;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.ContactRequestDTO;
import de.thnuernberg.sep.gruppe5.be.control.models.AssessmentMailModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Locale;

class MailTask extends Thread {
  private final JavaMailSender mailSender;
  private final SimpleMailMessage email;

  public MailTask(JavaMailSender mailSender, SimpleMailMessage email) {
    this.mailSender = mailSender;
    this.email = email;
  }

  public void run() {
    for (int i = 0; i < 3; i++) {
      try {
        mailSender.send(email);
        return;
      } catch (Exception e) {
        if (i == 2) {
          System.out.println("--- Start Fehler beim Senden einer Email ---");
          System.out.println(e.getMessage());
          System.out.println("Email-Objekt: " + email);
          System.out.println("--- Ende Fehler beim Senden einer Email ---");
        } else {
          System.out.println((i + 2) + ". Versuch Email zu versenden...");
        }
      }
    }
  }
}

@Service
@RequiredArgsConstructor
public class MailService {
  private final MessageSource messages;
  private final JavaMailSender mailSender;

  private void sendEmail(SimpleMailMessage email) {
    Thread mailTask = new MailTask(mailSender, email);
    mailTask.start();
  }

  public void sendVerificationMail(String recipientAddress, String token) {
    final SimpleMailMessage email = constructVerificationEmailMessage(recipientAddress, token);
    sendEmail(email);
  }

  public void sendPasswordResetMail(String recipientAddress, String token) {
    final SimpleMailMessage email = constructPasswordResetEmailMessage(recipientAddress, token);
    sendEmail(email);
  }

  public void sendContactMail(ContactRequestDTO contact) {
    final SimpleMailMessage email = constructContactEmailMessage(contact);
    sendEmail(email);
  }

  public void sendAssessmentResultsReadyMail(@Valid AssessmentMailModel assessment, String recipientAddress) {
    final SimpleMailMessage email = constructAssessmentResultsReadyEmailMessage(assessment, recipientAddress);
    sendEmail(email);
  }

  private SimpleMailMessage constructAssessmentResultsReadyEmailMessage(AssessmentMailModel assessment, String recipientAddress) {
    String course = assessment.getCourse();
    String season = assessment.getSemester().getSeason().toString();
    int year = assessment.getSemester().getYear();
    String lecturer = assessment.getLecturer();

    return constructEmailMessage(
      recipientAddress,
      String.format("Bewertung zum Kurs '%s' ist vorbei", course),
      String.format("Die Bewertungsphase zum Kurs '%s' im %s %s von %s ist nun vorbei. Sie können die Bewertungsergebnisse nun einsehen.", course, season, year, lecturer)
    );
  }

  private SimpleMailMessage constructVerificationEmailMessage(String recipientAddress, String token) {
    return constructEmailMessage(
      recipientAddress,
      "Bestätige deine Registrierung",
      "Du hast dich erfolgreich bei der Lehrveranstaltungs-Bewertungs-Platform der TH-Nürnberg registriert. " +
        "Um deine Registrierung abzuschließen und deinen Account freizuschalten, " +
        "klicke bitte auf den unteren Link " +
        "und gib in den nächsten 24 Stunden den Code aus dieser Email an." +
        " \r\n\n" +
        "http://localhost:4200/confirmRegistration" +
        " \r\n\n" +
        token +
        " \r\n\n" +
        "Wenn du dich nicht bei uns registriert hast, kannst du diese Email ignorieren " +
        "und dein Account wird nach 24 Stunden automatisiert gelöscht."
    );
  }

  private SimpleMailMessage constructPasswordResetEmailMessage(String recipientAddress, String token) {
    return constructEmailMessage(
      recipientAddress,
      "Passwort zurücksetzen",
      "Du hast die Änderung deines Passwortes auf der Lehrveranstaltungs-Bewertungs-Platform der TH- Nürnberg beantragt. " +
        "Um dein Passwort zu ändern, klicke bitte auf den unteren Link " +
        "und gib in den nächsten 24 Stunden den Code aus dieser Email " +
        "zusammen mit einem neuen Passwort an. " +
        "Bis dahin bleibt dein altes Passwort bestehen." +
        " \r\n\n" +
        "http://localhost:4200/renewPassword" +
        " \r\n\n" +
        token +
        " \r\n\n" +
        "Wenn du keine Änderung des Passworts beantragt hast, kannst du diese Email ignorieren " +
        "und dich wie gewohnt mit deinen normalen Anmeldedaten anmelden."
    );
  }

  private SimpleMailMessage constructContactEmailMessage(ContactRequestDTO contact) {
    return constructEmailMessage(
      "lehrveranstaltungen.bewerten@gmail.com",
      "Kontakt-Anfrage",
      "Jemand hat eine Kontaktanfrage über die Lehrveranstaltungs-Bewertungs-Plattform geschickt. " +
        " \r\n\n" +
        "Art des Anliegens: " +
        " \r\n\n" +
        contact.subject() +
        " \r\n\n" +
        "Text zum Anliegen: " +
        " \r\n\n" +
        contact.text() +
        " \r\n\n" +
        "Erreichen kann man die Person wie folgt: " +
        " \r\n\n" +
        contact.contactData() +
        " \r\n\n"
    );
  }

  private SimpleMailMessage constructEmailMessage(
    String recipientAddress,
    String subject,
    String message) {
    final SimpleMailMessage email = new SimpleMailMessage();
    //TODO: Dummy-Email-Adresse
    email.setTo(/*recipientAddress*/"lehrveranstaltungen.bewerten@gmail.com");
    email.setSubject(subject);
    email.setText(
      messages.getMessage(
        "message.regSuccLink",
        null,
        message,
        Locale.GERMAN
      )
    );
    return email;
  }
}
