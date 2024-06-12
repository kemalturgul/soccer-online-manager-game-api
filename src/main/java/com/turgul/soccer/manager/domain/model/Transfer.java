package com.turgul.soccer.manager.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transfer implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "player_id", referencedColumnName = "id")
  private Player player;

  @NotNull
  @Column(name = "market_value")
  private Long marketValue;

  @NotNull
  @Column(name = "requested_value")
  private Long requestedValue;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "current_team")
  private Team currentTeam;

  @NotNull private Boolean transferred;
}
