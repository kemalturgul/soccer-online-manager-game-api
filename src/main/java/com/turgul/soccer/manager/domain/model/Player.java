package com.turgul.soccer.manager.domain.model;

import com.turgul.soccer.manager.domain.PlayingPosition;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Player implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(name = "first_name")
  private String firstName;

  @NotNull
  @Column(name = "last_name")
  private String lastName;

  @NotNull private Integer age;

  @NotNull
  @Column(name = "market_value")
  private Long marketValue;

  @NotNull
  @Enumerated(EnumType.STRING)
  private PlayingPosition position;

  @ManyToOne(fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @JoinColumn(name = "team_id")
  private Team team;

  @Builder.Default
  @EqualsAndHashCode.Exclude
  @OneToMany(
      mappedBy = "player",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<Transfer> transfers = new HashSet<>();
}
