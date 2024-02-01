package de.thnuernberg.sep.gruppe5.be.utility.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DatePatternValidator implements ConstraintValidator<DatePattern, String> {

  @Override
  public boolean isValid(String deadline, ConstraintValidatorContext constraintValidatorContext) {
    return deadline.matches("^\\d{4}-\\d{2}-\\d{2}$");
  }
}
