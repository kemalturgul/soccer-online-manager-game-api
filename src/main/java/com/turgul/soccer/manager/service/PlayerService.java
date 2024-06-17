package com.turgul.soccer.manager.service;

import static com.turgul.soccer.manager.Constants.*;

import com.turgul.soccer.manager.domain.PlayingPosition;
import com.turgul.soccer.manager.domain.model.Player;
import com.turgul.soccer.manager.domain.model.Team;
import com.turgul.soccer.manager.repository.PlayerRepository;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class PlayerService {

  private final PlayerRepository playerRepository;

  public Player getPlayer(Long playerId) {
    return playerRepository
        .findById(playerId)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Player not found with Id:" + playerId));
  }

  public void removePlayer(Long playerId) {
    Player player =
        playerRepository
            .findById(playerId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Player not found with Id:" + playerId));
    playerRepository.deleteById(player.getId());
  }

  public Player createPlayer(
      Team team, String firstName, String lastName, PlayingPosition playingPosition) {
    return Player.builder()
        .firstName(firstName)
        .lastName(lastName)
        .age(getRandomAge())
        .position(playingPosition)
        .marketValue(DEFAULT_PLAYER_MARKET_VALUE)
        .team(team)
        .build();
  }

  private int getRandomAge() {
    return ThreadLocalRandom.current().nextInt(PLAYER_MIN_AGE, PLAYER_MAX_AGE);
  }
}
