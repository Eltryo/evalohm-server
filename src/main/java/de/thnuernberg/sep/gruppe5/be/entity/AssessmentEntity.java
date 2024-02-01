package de.thnuernberg.sep.gruppe5.be.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.AUTO;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "assessments")
public class AssessmentEntity {
  @Id
  @GeneratedValue(strategy = AUTO)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String course;

  @Embedded
  @Column(nullable = false)
  private SemesterEntity semester;

  @NotBlank
  @Column(nullable = false)
  private String lecturer;

  @Column(nullable = false)
  private LocalDate creationDate;

  @Column(nullable = false)
  private LocalDate deadline;

  @Column(nullable = false)
  private boolean expired;

  @Column(unique = true)
  private String reviewCode;

  @Pattern(regexp = "[A-Za-z0-9.]+@th-nuernberg\\.de", message = "TH-Mail")
  @Column(nullable = false)
  private String creator;

  @Column(nullable = false)
  private boolean closed;

  @PrePersist
  public void generateCode() {
    int length = 8;
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder code = new StringBuilder();

    for (int i = 0; i < length; i++) {
      int index = (int) (Math.random() * characters.length());
      code.append(characters.charAt(index));
    }

    this.reviewCode = code.toString();
  }

  //todo: implement reviews
}
