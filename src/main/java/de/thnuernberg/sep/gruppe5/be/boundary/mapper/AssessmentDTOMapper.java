package de.thnuernberg.sep.gruppe5.be.boundary.mapper;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.AssessmentRequestDTO;
import de.thnuernberg.sep.gruppe5.be.boundary.dtos.AssessmentResponseDTO;
import de.thnuernberg.sep.gruppe5.be.control.models.AssessmentModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssessmentDTOMapper {
  AssessmentResponseDTO toAssessmentDTO(AssessmentModel assessmentModel);

  @Mapping(target = "semester", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "expired", ignore = true)
  @Mapping(target = "reviewCode", ignore = true)
  @Mapping(target = "closed", ignore = true)
  @Mapping(target = "id", ignore = true)
  AssessmentModel toAssessmentModel(AssessmentRequestDTO assessmentRequestDTO);

  AssessmentModel toAssessmentModel(AssessmentResponseDTO assessmentResponseDTO);
}
