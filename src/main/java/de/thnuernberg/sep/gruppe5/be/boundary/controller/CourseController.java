package de.thnuernberg.sep.gruppe5.be.boundary.controller;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.CourseResponseDTO;
import de.thnuernberg.sep.gruppe5.be.boundary.mapper.CourseDTOMapper;
import de.thnuernberg.sep.gruppe5.be.control.models.Course;
import de.thnuernberg.sep.gruppe5.be.control.services.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseController {
  private final AssessmentService assessmentService;
  private final CourseDTOMapper courseDTOMapper;

  @GetMapping("/courses")
  public List<CourseResponseDTO> getCourses() {
    List<Course> result = assessmentService.getCourses();
    return result.stream().map(courseDTOMapper::toCourseDTO).toList();
  }
}
