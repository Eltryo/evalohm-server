package de.thnuernberg.sep.gruppe5.be.utility;

import de.thnuernberg.sep.gruppe5.be.entity.*;
import de.thnuernberg.sep.gruppe5.be.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AssessmentRepository assessmentRepository;
  private final PasswordEncoder passwordEncoder;
  private final SubmittedAssessmentRepository submittedAssessmentRepository;
  private final EvaluationRepository evaluationRepository;
  boolean alreadySetup = false;

  @Override
  @Transactional
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (alreadySetup)
      return;

    setupRoles();
    setupUsers();
    setupAssessments();
    setupEvaluation();

    alreadySetup = true;
  }

  private void setupEvaluation() {
    for (int i = 0; i < 3; i++) {
      SubmittedAssessmentEntity submittedAssessmentEntity = new SubmittedAssessmentEntity();
      submittedAssessmentEntity.setUserId(2);
      submittedAssessmentEntity.setAssessmentId(1L);
      submittedAssessmentRepository.save(submittedAssessmentEntity);


      EvaluationEntity evaluation = new EvaluationEntity();
      evaluation.setAssessmentId(1L);
      evaluation.setVorlesungsRating(generateRandomScale());
      evaluation.setUebungsRating(generateRandomScale());
      evaluation.setUnterlagenRating(generateRandomScale());
      evaluation.setPruefungsRating(generateRandomScale());
      evaluation.setZeitaufwandRating(generateRandomScale());
      evaluation.setInhaltRating(generateRandomScale());
      evaluation.setStoffmengeRating(generateRandomScale());
      evaluation.setNiveauRating(generateRandomScale());
      evaluation.setRelevanzRating(generateRandomScale());
      evaluation.setBemerkung(generateComment(i));
      evaluationRepository.save(evaluation);
    }
  }

  private String generateComment(int i) {
    String[] comments = {
      "Die Lehrveranstaltung war echt super!",
      "Das Hochladen der Lösungen zu den Aufgaben wäre echt toll gewesen, aber ansonsten war der Kurs echt gut.",
      "Nächstes mal das Mikrofon benutzen"
    };

    return comments[i];
  }

  private int generateRandomScale() {
    return 1 + (int) (Math.random() * ((5 - 1) + 1));
  }

  private void setupRoles() {
    createRoleIfNotFound("PROF");
    createRoleIfNotFound("STUDENT");
  }

  private void createRoleIfNotFound(String name) {
    Optional<RoleEntity> role = roleRepository.findByAuthority(name);

    if (role.isEmpty()) {
      RoleEntity result = new RoleEntity();
      result.setAuthority(name);
      roleRepository.save(result);
    }
  }

  private void setupUsers() {
    createUserIfNotFound("prof@th-nuernberg.de", "password", "PROF");
    createUserIfNotFound("student@th-nuernberg.de", "password", "STUDENT");
  }

  private void createUserIfNotFound(String mail, String password, String roleName) {
    Optional<UserEntity> userOptional = userRepository.findByUsername(mail);

    if (userOptional.isEmpty()) {
      RoleEntity role = roleRepository.findByAuthority(roleName).get();
      UserEntity user = new UserEntity();
      user.setPassword(passwordEncoder.encode(password));
      user.setUsername(mail);
      user.setAuthorities(Set.of(role));
      user.setEnabled(true);
      userRepository.save(user);
    }
  }

  private void setupAssessments() {
    createAssessmentIfNotFound("Test", "Müller");
  }

  private void createAssessmentIfNotFound(String courseName, String lecturer) {
    Optional<AssessmentEntity> assessment = assessmentRepository.findById(1L);

    if (assessment.isEmpty()) {
      SemesterEntity semester = new SemesterEntity();
      semester.setSeason(Season.Winter);
      semester.setYear(2023);

      AssessmentEntity result = new AssessmentEntity();
      result.setCourse(courseName);
      result.setLecturer(lecturer);
      result.setCreator("prof@th-nuernberg.de");
      result.setExpired(false);
      result.setSemester(semester);
      result.setCreationDate(LocalDate.now().minusDays(8));
      result.setDeadline(LocalDate.now().minusDays(1));
      assessmentRepository.save(result);
    }

    Optional<AssessmentEntity> expiredAssessment = assessmentRepository.findById(2L);

    if (expiredAssessment.isEmpty()) {
      SemesterEntity semester = new SemesterEntity();
      semester.setSeason(Season.Sommer);
      semester.setYear(2023);

      AssessmentEntity result = new AssessmentEntity();
      result.setCourse(courseName);
      result.setLecturer(lecturer);
      result.setCreator("prof@th-nuernberg.de");
      result.setExpired(true);
      result.setSemester(semester);
      result.setCreationDate(LocalDate.of(2023, 7, 1));
      result.setDeadline(LocalDate.of(2023, 7, 30));
      assessmentRepository.save(result);
    }
  }
}
