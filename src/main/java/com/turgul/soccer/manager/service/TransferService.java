package com.turgul.soccer.manager.service;

import com.turgul.soccer.manager.domain.model.Player;
import com.turgul.soccer.manager.domain.model.Team;
import com.turgul.soccer.manager.domain.model.Transfer;
import com.turgul.soccer.manager.domain.model.Users;
import com.turgul.soccer.manager.repository.PlayerRepository;
import com.turgul.soccer.manager.repository.TeamRepository;
import com.turgul.soccer.manager.repository.TransferRepository;
import com.turgul.soccer.manager.repository.UserRepository;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class TransferService {

  private final TransferRepository transferRepository;
  private final TeamRepository teamRepository;
  private final PlayerRepository playerRepository;
  private final UserRepository userRepository;

  public List<Transfer> getTransfers() {
    return transferRepository.findAll();
  }

  public List<Transfer> getAvailableToTransfer() {
    return transferRepository.findAllByTransferredFalse();
  }

  public Transfer getTransfer(Long transferId) {
    return transferRepository
        .findById(transferId)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Could not find a transfer with Id:" + transferId));
  }

  public Transfer createTransfer(Long playerId, Long requestedPrice, Principal principal) {
    Player player =
        playerRepository
            .findById(playerId)
            .filter(p -> p.getTeam().getUser().getUsername().equals(principal.getName()))
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Player not found for this user, playerId:" + playerId));

    if (transferRepository.findByPlayerIdAndTransferredFalse(playerId).isPresent()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Transfer already exists for playerId:" + playerId);
    }

    return transferRepository.save(createTransfer(player, requestedPrice));
  }

  private Transfer createTransfer(Player player, Long requestedPrice) {
    return Transfer.builder()
        .player(player)
        .requestedValue(requestedPrice)
        .marketValue(player.getMarketValue())
        .currentTeam(player.getTeam())
        .transferred(Boolean.FALSE)
        .build();
  }

  public void deleteTransfer(Long transferId) {
    Transfer transfer =
        transferRepository
            .findById(transferId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Transfer not found, transferId:" + transferId));

    Player player = transfer.getPlayer();

    if (player != null) {
      transfer.setPlayer(null);
      player.getTransfers().remove(transfer);
      playerRepository.save(player);
    }

    transferRepository.deleteById(transferId);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void transferPlayer(Long transferId, String userName) {
    Transfer transfer =
        transferRepository
            .findByIdAndTransferredFalse(transferId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Transfer not found transferId:" + transferId));

    Users user =
        userRepository
            .findByEmail(userName)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found userName:" + userName));

    Team newTeamOfPlayer = user.getTeam();

    if (Objects.equals(newTeamOfPlayer.getId(), transfer.getPlayer().getTeam().getId())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Team destination must be different from current team");
    }

    if (newTeamOfPlayer.getTeamCash() < transfer.getRequestedValue()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Insufficient budget to transfer this player, teamCash:" + newTeamOfPlayer.getTeamCash());
    }

    transferRepository.updateTransfer(newTeamOfPlayer, Boolean.TRUE, transferId);
    playerRepository.updatePlayer(
        newTeamOfPlayer, transfer.getRequestedValue(), transfer.getPlayer().getId());

    // Update new player's team
    long teamCash = newTeamOfPlayer.getTeamCash() - transfer.getRequestedValue();
    long teamValue = newTeamOfPlayer.calculateTeamValue();
    teamRepository.updateTeamCashAndValue(teamValue, teamCash, newTeamOfPlayer.getId());

    // Update previous team
    teamCash = transfer.getCurrentTeam().getTeamCash() + transfer.getRequestedValue();
    teamValue = transfer.getCurrentTeam().calculateTeamValue();
    teamRepository.updateTeamCashAndValue(teamValue, teamCash, transfer.getCurrentTeam().getId());
  }
}
