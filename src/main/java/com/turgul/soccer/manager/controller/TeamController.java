package com.turgul.soccer.manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turgul.soccer.manager.dto.request.TeamUpdateRequest;
import com.turgul.soccer.manager.dto.response.TeamResponse;
import com.turgul.soccer.manager.mapper.TeamMapper;
import com.turgul.soccer.manager.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Team", description = "Team management APIs")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

  private final TeamService teamService;
  private final TeamMapper teamMapper;
  private final ObjectMapper objectMapper;

  @Operation(
      summary = "Get team detail",
      description = "Returns team information for given id",
      tags = {"Get"})
  @Parameter(name = "teamId", description = "Id of the team to get its detail", required = true)
  @GetMapping("/{teamId}")
  public ResponseEntity<TeamResponse> getTeam(@PathVariable Long teamId, Principal principal) {
    log.info("getTeam request received for teamId:{}", teamId);
    Optional<TeamResponse> teamResponse =
        teamService.getTeam(teamId, principal).map(teamMapper::map);

    return teamResponse
        .map(ResponseEntity::ok)
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @Operation(
      summary = "Get team detail",
      description = "Returns team information for given id",
      tags = {"Get"})
  @Parameter(name = "teamId", description = "Id of the team to get its details", required = true)
  @GetMapping("/team-of-user")
  public ResponseEntity<TeamResponse> getTeam(Principal principal) {
    log.info("getTeam request received");
    Optional<TeamResponse> teamResponse =
        teamService.getTeam(principal.getName()).map(teamMapper::map);

    return teamResponse
        .map(ResponseEntity::ok)
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @Operation(
      summary = "Update team data",
      description = "Updates team attributes with requested new data for given id",
      tags = {"Put"})
  @PutMapping("/update")
  public ResponseEntity<TeamResponse> updateTeam(
      @Valid @RequestBody TeamUpdateRequest updateRequest, Principal principal)
      throws JsonProcessingException {

    log.info(
        "getTeam request received for updateRequest:{}",
        objectMapper.writeValueAsString(updateRequest));

    Optional<TeamResponse> updatedTeam =
        teamService.updateTeam(updateRequest, principal).map(teamMapper::map);

    return updatedTeam
        .map(ResponseEntity::ok)
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
