package de.thnuernberg.sep.gruppe5.be.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Builder
@Getter
@Setter
@ToString
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "evaluations")
public class EvaluationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  private Long assessmentId;

  @Min(1)
  @Max(5)
  private int vorlesungsRating;

  @Min(1)
  @Max(5)
  private int uebungsRating;

  @Min(1)
  @Max(5)
  private int unterlagenRating;

  @Min(1)
  @Max(5)
  private int pruefungsRating;

  @Min(0)
  @Max(5)
  private int zeitaufwandRating;

  @Min(0)
  @Max(5)
  private int inhaltRating;

  @Min(0)
  @Max(5)
  private int stoffmengeRating;

  @Min(0)
  @Max(5)
  private int niveauRating;

  @Min(0)
  @Max(5)
  private int relevanzRating;

  private String bemerkung;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    EvaluationEntity that = (EvaluationEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
