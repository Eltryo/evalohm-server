package de.thnuernberg.sep.gruppe5.be.boundary.mapper;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.EvaluationDTO;
import de.thnuernberg.sep.gruppe5.be.control.models.Evaluation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EvaluationDTOMapper {
  Evaluation toEvaluation(EvaluationDTO evaluationDTO);

  EvaluationDTO toEvaluationDTO(Evaluation evaluation);
}
