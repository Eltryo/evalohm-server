package de.thnuernberg.sep.gruppe5.be.control.mapper;

import de.thnuernberg.sep.gruppe5.be.control.models.Evaluation;
import de.thnuernberg.sep.gruppe5.be.entity.EvaluationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EvaluationMapper {
  EvaluationEntity toEvaluationEntity(Evaluation evaluation);

  Evaluation toEvaluation(EvaluationEntity evaluationEntity);
}
