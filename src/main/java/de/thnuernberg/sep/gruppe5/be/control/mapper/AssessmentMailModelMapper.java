package de.thnuernberg.sep.gruppe5.be.control.mapper;

import de.thnuernberg.sep.gruppe5.be.control.models.AssessmentMail;
import de.thnuernberg.sep.gruppe5.be.entity.AssessmentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssessmentMailModelMapper {
  AssessmentMail toAssessmentMailModel(AssessmentEntity savedAssessment);
}
