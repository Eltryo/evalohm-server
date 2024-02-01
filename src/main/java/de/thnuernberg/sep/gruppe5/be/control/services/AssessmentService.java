package de.thnuernberg.sep.gruppe5.be.control.services;

import de.thnuernberg.sep.gruppe5.be.control.mapper.AssessmentMailModelMapper;
import de.thnuernberg.sep.gruppe5.be.control.mapper.AssessmentModelMapper;
import de.thnuernberg.sep.gruppe5.be.control.models.Assessment;
import de.thnuernberg.sep.gruppe5.be.control.models.AssessmentMail;
import de.thnuernberg.sep.gruppe5.be.control.models.Course;
import de.thnuernberg.sep.gruppe5.be.control.models.Semester;
import de.thnuernberg.sep.gruppe5.be.entity.AssessmentEntity;
import de.thnuernberg.sep.gruppe5.be.entity.Season;
import de.thnuernberg.sep.gruppe5.be.entity.SemesterEntity;
import de.thnuernberg.sep.gruppe5.be.entity.UserEntity;
import de.thnuernberg.sep.gruppe5.be.repositories.AssessmentRepository;
import de.thnuernberg.sep.gruppe5.be.repositories.SubmittedAssessmentRepository;
import de.thnuernberg.sep.gruppe5.be.repositories.UserRepository;
import de.thnuernberg.sep.gruppe5.be.utility.exceptions.AppException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssessmentService {
  private final AssessmentRepository assessmentRepository;
  private final AssessmentModelMapper assessmentModelMapper;
  private final UserRepository userRepository;
  private final SubmittedAssessmentRepository submittedAssessmentRepository;
  private final MailService mailService;
  private final AssessmentMailModelMapper assessmentMailModelMapper;

  private static boolean isDeadlineValid(LocalDate deadline, @Valid Semester semester) {
    LocalDate lowerLimit = LocalDate.now().plusDays(6);
    LocalDate upperLimit;
    int year = semester.getYear();
    if (semester.getSeason() == Season.Sommer)
      upperLimit = LocalDate.of(year, Month.SEPTEMBER, 10);
    else
      upperLimit = LocalDate.of(year + 1, Month.MARCH, 15);

    return deadline.isAfter(lowerLimit) && deadline.isBefore(upperLimit);
  }

  private static int getMonthDay(LocalDate date) {
    return date.getMonthValue() * 100 + date.getDayOfMonth();
  }

  private static boolean isSummerSeason(
    int currentMonthDay,
    int startMonthDay,
    int endMonthDay
  ) {
    return (currentMonthDay >= startMonthDay) && (currentMonthDay <= endMonthDay);
  }

  public Assessment addAssessment(@Valid Assessment assessmentModel) {
    String course = assessmentModel.getCourse();
    String lecturer = assessmentModel.getLecturer();
    LocalDate deadline = assessmentModel.getDeadline();

    LocalDate currentDate = LocalDate.now();
    Semester semesterModel = new Semester(identifyCurrentSeason(currentDate), currentDate.getYear());
    if (!isDeadlineValid(deadline, semesterModel))
      throw new AppException("Das Enddatum ist nicht valide", HttpStatus.BAD_REQUEST);

    assessmentModel.setSemester(semesterModel);
    assessmentModel.setCreationDate(currentDate);
    AssessmentEntity assessmentEntity = assessmentModelMapper.toAssessmentEntity(assessmentModel);

    SemesterEntity semester = assessmentEntity.getSemester();
    Optional<AssessmentEntity> allByCourseAndSemesterAndLecturer = assessmentRepository.findAllByCourseAndSemesterAndLecturer(course, semester, lecturer);
    if (allByCourseAndSemesterAndLecturer.isPresent())
      throw new AppException("Evaluation existiert bereits", HttpStatus.BAD_REQUEST);

    UserEntity loggedInUser = this.userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
      .orElseThrow(() -> new AppException("Der angemeldete Benutzer konnte nicht gefunden werden", HttpStatus.INTERNAL_SERVER_ERROR));

    assessmentEntity.setCreator(loggedInUser.getUsername());
    AssessmentEntity savedAssessment = assessmentRepository.save(assessmentEntity);

    return assessmentModelMapper.toAssessmentModel(savedAssessment);
  }

  public List<Assessment> getAllAssessments() {
    List<AssessmentEntity> assessments = assessmentRepository.findAll();

    return assessments.stream().map(assessmentModelMapper::toAssessmentModel).toList();
  }

  public Assessment getAssessmentById(Long assessmentId) {
    AssessmentEntity assessment = assessmentRepository.findById(assessmentId)
      .orElseThrow(() -> new AppException("Diese Evaluation existiert nicht", HttpStatus.NOT_FOUND));

    return assessmentModelMapper.toAssessmentModel(assessment);
  }

  public Assessment updateAssessment(Assessment assessment) {
    AssessmentEntity assessmentEntity = assessmentRepository.findById(assessment.getId())
      .orElseThrow(() -> new AppException("Diese Evaluation existiert nicht", HttpStatus.NOT_FOUND));

    UserEntity loggedInUser = this.userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
      .orElseThrow(() -> new AppException("Der angemeldete Benutzer konnte nicht gefunden werden", HttpStatus.INTERNAL_SERVER_ERROR));

    if (!assessmentEntity.getCreator().equals(loggedInUser.getUsername()))
      throw new AppException("Dafür hast du nicht die benötigten Berechtigungen!", HttpStatus.FORBIDDEN);

    assessmentEntity.setCourse(assessment.getCourse());
    assessmentEntity.setLecturer(assessment.getLecturer());
    assessmentEntity.setDeadline(assessment.getDeadline());

    return assessmentModelMapper.toAssessmentModel(assessmentRepository.save(assessmentEntity));
  }

  public List<Assessment> getCreatedAssessmentsFromLoggedInUser() {
    UserEntity loggedInUser = this.userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
      .orElseThrow(() -> new AppException("Der angemeldete Benutzer konnte nicht gefunden werden", HttpStatus.INTERNAL_SERVER_ERROR));

    List<AssessmentEntity> allByCourse = assessmentRepository.findAllByCreator(loggedInUser.getUsername());

    return allByCourse.stream().map(assessmentModelMapper::toAssessmentModel).toList();
  }

  public List<Course> getCourses() {
    List<Assessment> allAssessments = assessmentRepository.findAll().stream().map(assessmentModelMapper::toAssessmentModel).toList();

    List<String> courseNames = new ArrayList<>();
    List<Course> courses = new ArrayList<>();

    for (Assessment assessment : allAssessments) {
      if (!courseNames.contains(assessment.getCourse())) {
        courseNames.add(assessment.getCourse());
        courses.add(new Course(assessment.getCourse()));
      }
    }

    return courses;
  }

  public void closeAssessment(Long id) {
    AssessmentEntity assessment = assessmentRepository.findById(id)
      .orElseThrow(() -> new AppException("Diese Evaluation existiert nicht", HttpStatus.NOT_FOUND));

    if (assessment.isClosed() || assessment.isExpired())
      throw new AppException("Diese Evaluation ist bereits beendet", HttpStatus.BAD_REQUEST);

    LocalDate currentDate = LocalDate.now();
    LocalDate creationDate = assessment.getCreationDate();
    if (currentDate.isBefore(creationDate.plusDays(7)))
      throw new AppException("Die Evaluation kann noch nicht geschlossen werden", HttpStatus.BAD_REQUEST);


    assessment.setClosed(true);
    assessment.setDeadline(currentDate);
    AssessmentEntity closedAssessment = assessmentRepository.save(assessment);
    notifyEvaluators(closedAssessment);
  }

  void notifyEvaluators(AssessmentEntity assessment) {
    if (assessment.isClosed() && assessment.isExpired()) {
      return;
    }

    Optional<List<Integer>> userIdsByAssessmentId = submittedAssessmentRepository.findUserIdsByAssessmentId(assessment.getId());
    if (userIdsByAssessmentId.isPresent()) {
      for (Integer userId : userIdsByAssessmentId.get()) {
        Optional<String> usernameById = userRepository.getUsernameById(userId);
        if (usernameById.isPresent()) {
          AssessmentMail assessmentMailModel = assessmentMailModelMapper.toAssessmentMailModel(assessment);
          mailService.sendAssessmentResultsReadyMail(assessmentMailModel, usernameById.get());
        }
      }
    }

    AssessmentMail assessmentMailModel = assessmentMailModelMapper.toAssessmentMailModel(assessment);
    mailService.sendAssessmentResultsReadyMail(assessmentMailModel, assessment.getCreator());
  }

  Season identifyCurrentSeason(LocalDate currentTime) {
    LocalDate startDate = LocalDate.of(1, Month.MARCH, 15);
    LocalDate endDate = LocalDate.of(1, Month.SEPTEMBER, 30);

    int currentMonthDay = getMonthDay(currentTime);
    int firstSummerMonthDay = getMonthDay(startDate);
    int lastSummerMonthDay = getMonthDay(endDate);

    return isSummerSeason(
      currentMonthDay,
      firstSummerMonthDay,
      lastSummerMonthDay
    ) ? Season.Sommer : Season.Winter;
  }

  public List<Long> getSubmittedAssessmentsFromLoggedInUser() {
    UserEntity user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
      .orElseThrow(() -> new AppException("Der angemeldete Benutzer konnte nicht gefunden werden", HttpStatus.INTERNAL_SERVER_ERROR));

    return submittedAssessmentRepository.findAssessmentIdsByUserIds(user.getId());
  }
}
