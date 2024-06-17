package com.turgul.soccer.manager.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turgul.soccer.manager.domain.PlayingPosition;
import com.turgul.soccer.manager.domain.model.Player;
import com.turgul.soccer.manager.domain.model.Team;
import com.turgul.soccer.manager.dto.response.PlayerResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PlayerMapperTest {
  private final PlayerMapper tested = Mappers.getMapper(PlayerMapper.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void allFieldsAreMapped() {
    // Given
    var player = createPlayer();
    var expected =
        PlayerResponse.builder()
            .id(1L)
            .age(25)
            .firstName("FirstName")
            .lastName("LastName")
            .marketValue(10000L)
            .teamId(2L)
            .position(PlayingPosition.ATTACKER)
            .build();

    // When
    var dto = tested.map(player);

    // Then
    assertThat(dto).isEqualTo(expected);
  }

  @Test
  void noFieldIsSkipped() throws JsonProcessingException {
    // Given
    var player = createPlayer();

    // When
    var dto = tested.map(player);

    // Then
    var serialized = objectMapper.writeValueAsString(dto);
    assertThat(serialized).doesNotContain("null");
  }

  @Test
  void nullsAreMappedToNulls() {
    // Given
    var player = Player.builder().build();

    // When
    var dto = tested.map(player);

    // Then
    var expected = PlayerResponse.builder().build();
    assertThat(dto).isEqualTo(expected);
  }

  private Player createPlayer() {
    var team = Team.builder().id(2L).name("Test Team").country("Turkey").teamCash(100000L).build();
    return Player.builder()
        .id(1L)
        .age(25)
        .firstName("FirstName")
        .lastName("LastName")
        .marketValue(10000L)
        .team(team)
        .position(PlayingPosition.ATTACKER)
        .build();
  }
}
