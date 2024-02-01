package de.thnuernberg.sep.gruppe5.be.control.mapper;

import de.thnuernberg.sep.gruppe5.be.control.models.Assessment;
import de.thnuernberg.sep.gruppe5.be.entity.AssessmentEntity;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssessmentModelMapper {
  Assessment toAssessmentModel(AssessmentEntity savedAssessment);

  @Mapping(target = "creator", ignore = true)
  AssessmentEntity toAssessmentEntity(@Valid Assessment assessmentModel);
}
