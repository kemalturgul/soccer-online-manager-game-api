package com.turgul.soccer.manager.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turgul.soccer.manager.domain.PlayingPosition;
import com.turgul.soccer.manager.domain.model.Player;
import com.turgul.soccer.manager.domain.model.Team;
import com.turgul.soccer.manager.domain.model.Transfer;
import com.turgul.soccer.manager.dto.response.TransferResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TransferMapperTest {
  private final TransferMapper tested = Mappers.getMapper(TransferMapper.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void allFieldsAreMapped() {
    // Given
    var transfer = createTransfer(1L, 5L, 7L, Boolean.FALSE, 5000L);

    var expected = createTransferResponse(1L, 5L, 7L, Boolean.FALSE, 5000L);

    // When
    var dto = tested.map(transfer);

    // Then
    assertThat(dto).isEqualTo(expected);
  }

  @Test
  void noFieldIsSkipped() throws JsonProcessingException {
    // Given
    var transfer = createTransfer(4L, 6L, 3L, Boolean.FALSE, 30000L);

    // When
    var dto = tested.map(transfer);

    // Then
    var serialized = objectMapper.writeValueAsString(dto);
    assertThat(serialized).doesNotContain("null");
  }

  @Test
  void nullsAreMappedToNulls() {
    // Given
    var transfer = Transfer.builder().build();

    // When
    var dto = tested.map(transfer);

    // Then
    var expected = TransferResponse.builder().build();
    assertThat(dto).isEqualTo(expected);
  }

  @Test
  void allFieldsAreMappedForList() {
    // Given
    var transfer1 = createTransfer(1L, 5L, 7L, Boolean.FALSE, 5000L);
    var transfer2 = createTransfer(4L, 2L, 10L, Boolean.TRUE, 3000L);
    var transfers = List.of(transfer1, transfer2);

    var transferResponse1 = createTransferResponse(1L, 5L, 7L, Boolean.FALSE, 5000L);
    var transferResponse2 = createTransferResponse(4L, 2L, 10L, Boolean.TRUE, 3000L);
    var expectedList = List.of(transferResponse1, transferResponse2);

    // When
    var dtoList = tested.map(transfers);

    // Then
    assertThat(dtoList).containsExactlyInAnyOrderElementsOf(expectedList);
  }

  @Test
  void whenListIsEmptyThenReturnsEmptyList() {
    // Given
    // When
    var dtoList = tested.map(List.of());

    // Then
    assertThat(dtoList).isEmpty();
  }

  Transfer createTransfer(
      Long transferId, Long playerId, Long teamId, Boolean transferred, Long marketValue) {
    var player =
        Player.builder()
            .id(playerId)
            .age(25)
            .firstName("FirstName")
            .lastName("LastName")
            .marketValue(marketValue)
            .position(PlayingPosition.GOALKEEPER)
            .build();
    var team =
        Team.builder().id(teamId).name("Test Team").country("Turkey").teamCash(100000L).build();

    return Transfer.builder()
        .id(transferId)
        .marketValue(marketValue)
        .requestedValue(7000L)
        .transferred(transferred)
        .player(player)
        .currentTeam(team)
        .build();
  }

  private TransferResponse createTransferResponse(
      Long transferId, Long playerId, Long teamId, Boolean transferred, Long marketValue) {
    return TransferResponse.builder()
        .id(transferId)
        .playerId(playerId)
        .currentTeamId(teamId)
        .transferred(transferred)
        .requestedValue(7000L)
        .marketValue(marketValue)
        .build();
  }
}
