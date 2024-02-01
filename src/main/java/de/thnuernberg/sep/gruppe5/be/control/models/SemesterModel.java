package de.thnuernberg.sep.gruppe5.be.control.models;

import de.thnuernberg.sep.gruppe5.be.entity.Season;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SemesterModel {
  @NotNull
  private Season season;
  private int year;
}
