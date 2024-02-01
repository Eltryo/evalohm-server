package de.thnuernberg.sep.gruppe5.be.boundary.mapper;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.CourseResponseDTO;
import de.thnuernberg.sep.gruppe5.be.control.models.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseDTOMapper {
  CourseResponseDTO toCourseDTO(Course course);
}
