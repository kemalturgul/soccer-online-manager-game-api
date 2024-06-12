package com.turgul.soccer.manager.controller;

import com.turgul.soccer.manager.dto.response.PlayerResponse;
import com.turgul.soccer.manager.mapper.PlayerMapper;
import com.turgul.soccer.manager.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Player", description = "Player management APIs")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

  private final PlayerService playerService;
  private final PlayerMapper playerMapper;

  @Operation(
      summary = "Get player detail",
      description = "Returns player information with given id.",
      tags = {"Get"})
  @Parameter(
      name = "playerId",
      description = "Id of the player to retrieve detail",
      required = true)
  @GetMapping("/{playerId}")
  public PlayerResponse getPlayer(@PathVariable Long playerId) {
    log.info("getPlayer request received for playerId:{}", playerId);
    return playerMapper.map(playerService.getPlayer(playerId));
  }

  @Operation(
      summary = "Remove player",
      description = "Deletes player with given id.",
      tags = {"Delete"})
  @Parameter(name = "playerId", description = "Id of the player to be deleted", required = true)
  @DeleteMapping("/{playerId}")
  public ResponseEntity<Void> deletePlayer(@PathVariable Long playerId) {
    log.info("deletePlayer request received for playerId:{}", playerId);
    playerService.removePlayer(playerId);
    return ResponseEntity.ok().build();
  }
}
