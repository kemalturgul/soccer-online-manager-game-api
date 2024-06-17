package com.turgul.soccer.manager.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turgul.soccer.manager.domain.PlayingPosition;
import com.turgul.soccer.manager.domain.model.Player;
import com.turgul.soccer.manager.domain.model.Team;
import com.turgul.soccer.manager.dto.response.PlayerResponse;
import com.turgul.soccer.manager.dto.response.TeamResponse;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TeamMapperTest {
  private final TeamMapper tested = Mappers.getMapper(TeamMapper.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void allFieldsAreMapped() {
    // Given
    var playerResponse =
        PlayerResponse.builder()
            .id(1L)
            .firstName("Player-1")
            .lastName("Goalkeeper")
            .position(PlayingPosition.GOALKEEPER)
            .age(25)
            .marketValue(5000L)
            .build();

    var team = createTeam();
    var expected =
        TeamResponse.builder()
            .id(5L)
            .name("Superstars Club")
            .country("England")
            .teamValue(125000L)
            .teamCash(500000L)
            .players(List.of(playerResponse))
            .build();

    // When
    var dto = tested.map(team);

    // Then
    assertThat(dto).isEqualTo(expected);
  }

  @Test
  void nullsAreMappedToNulls() {
    // Given
    var team = Team.builder().build();

    // When
    var dto = tested.map(team);

    // Then
    var expected = TeamResponse.builder().players(List.of()).build();
    assertThat(dto).isEqualTo(expected);
  }

  private Team createTeam() {
    var team =
        Team.builder()
            .id(5L)
            .name("Superstars Club")
            .country("England")
            .teamCash(500000L)
            .teamValue(125000L)
            .players(Set.of())
            .build();
    var player =
        Player.builder()
            .id(1L)
            .firstName("Player-1")
            .lastName("Goalkeeper")
            .position(PlayingPosition.GOALKEEPER)
            .age(25)
            .marketValue(5000L)
            .team(team)
            .build();

    return team.withPlayers(Set.of(player));
  }
}
