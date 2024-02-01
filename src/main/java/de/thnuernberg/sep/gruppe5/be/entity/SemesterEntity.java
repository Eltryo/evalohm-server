package de.thnuernberg.sep.gruppe5.be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SemesterEntity {
  @Enumerated(EnumType.STRING)
  private Season season;

  //todo eventually change from int to String
  @Column(name = "period", nullable = false)
  private int year;
}

