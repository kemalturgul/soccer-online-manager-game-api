package com.turgul.soccer.manager.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.springframework.util.CollectionUtils;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Team implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private String name;

  @NotNull private String country;

  @NotNull
  @Column(name = "team_cash")
  private Long teamCash;

  @Column(name = "team_value")
  private Long teamValue;

  @EqualsAndHashCode.Exclude
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  private Users user;

  @EqualsAndHashCode.Exclude
  @Builder.Default
  @OneToMany(
      mappedBy = "team",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<Player> players = new HashSet<>();

  @EqualsAndHashCode.Exclude
  @Builder.Default
  @OneToMany(
      mappedBy = "currentTeam",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<Transfer> transfers = new HashSet<>();

  public Long calculateTeamValue() {
    return CollectionUtils.isEmpty(players)
        ? 0L
        : players.stream().mapToLong(Player::getMarketValue).sum();
  }
}
