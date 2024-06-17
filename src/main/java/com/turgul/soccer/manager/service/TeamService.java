package com.turgul.soccer.manager.service;

import static com.turgul.soccer.manager.Constants.DEFAULT_TEAM_BUDGET;
import static com.turgul.soccer.manager.Constants.DEFAULT_TEAM_COUNTRY;

import com.turgul.soccer.manager.domain.PlayingPosition;
import com.turgul.soccer.manager.domain.model.Player;
import com.turgul.soccer.manager.domain.model.Team;
import com.turgul.soccer.manager.domain.model.Users;
import com.turgul.soccer.manager.dto.request.TeamUpdateRequest;
import com.turgul.soccer.manager.repository.TeamRepository;
import com.turgul.soccer.manager.repository.UserRepository;
import java.security.Principal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class TeamService {

  private final PlayerService playerService;
  private final TeamRepository teamRepository;
  private final UserRepository userRepository;

  public Optional<Team> getTeam(String userEmail) {
    var user =
        userRepository
            .findByEmail(userEmail)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found to get its team!"));

    return teamRepository.findById(user.getTeam().getId());
  }

  public Optional<Team> getTeam(Long teamId, Principal principal) {
    return teamRepository
        .findById(teamId)
        .filter(team -> team.getUser().getUsername().equals(principal.getName()));
  }

  public Optional<Team> updateTeam(TeamUpdateRequest updateRequest, Principal principal) {
    Optional<Team> currentTeam =
        teamRepository
            .findById(updateRequest.teamId())
            .filter(team -> team.getUser().getUsername().equals(principal.getName()));

    if (currentTeam.isEmpty()) {
      return Optional.empty();
    }

    Team team = currentTeam.get();
    team.setName(updateRequest.name());
    team.setCountry(updateRequest.country());
    team.setTeamValue(updateRequest.teamValue());

    return Optional.of(teamRepository.save(team));
  }

  public Team createTeam(Users user) {
    Team team =
        Team.builder()
            .name(createTeamName(user.getFirstName(), user.getLastName()))
            .country(DEFAULT_TEAM_COUNTRY)
            .teamCash(DEFAULT_TEAM_BUDGET)
            .user(user)
            .build();

    createRandomPlayers(team);
    team.setTeamValue(team.calculateTeamValue());

    return team;
  }

  private void createRandomPlayers(Team team) {
    team.getPlayers().addAll(createPlayers(team, PlayingPosition.GOALKEEPER, 3));
    team.getPlayers().addAll(createPlayers(team, PlayingPosition.DEFENDER, 5));
    team.getPlayers().addAll(createPlayers(team, PlayingPosition.MIDFIELDER, 6));
    team.getPlayers().addAll(createPlayers(team, PlayingPosition.ATTACKER, 6));
  }

  private Set<Player> createPlayers(
      Team team, PlayingPosition playingPosition, int numberOfPlayer) {
    return IntStream.rangeClosed(1, numberOfPlayer)
        .mapToObj(
            num ->
                playerService.createPlayer(
                    team, playingPosition.name(), "Player-" + num, playingPosition))
        .collect(Collectors.toSet());
  }

  private String createTeamName(String firstName, String lastName) {
    return String.format("Team of %s %s", firstName, lastName);
  }
}
