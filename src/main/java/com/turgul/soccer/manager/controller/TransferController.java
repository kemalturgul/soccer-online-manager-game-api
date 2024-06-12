package com.turgul.soccer.manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turgul.soccer.manager.domain.model.Transfer;
import com.turgul.soccer.manager.dto.request.TransferRequest;
import com.turgul.soccer.manager.dto.response.AddPlayerToTransferListResponse;
import com.turgul.soccer.manager.dto.response.TransferResponse;
import com.turgul.soccer.manager.mapper.TransferMapper;
import com.turgul.soccer.manager.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transfer", description = "Transfer management APIs")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

  private final TransferService transferService;
  private final TransferMapper transferMapper;
  private final ObjectMapper objectMapper;

  @Operation(
      summary = "Get all defined transfers",
      description = "Returns all created transfers",
      tags = {"Get"})
  @GetMapping("/all")
  public List<TransferResponse> getAllTransfers() {
    log.info("getAllTransfers request received");
    List<Transfer> transfers = transferService.getTransfers();
    return transferMapper.map(transfers);
  }

  @Operation(
      summary = "Get all open transfers",
      description = "Returns all created transfers which are not transferred yet",
      tags = {"Get"})
  @GetMapping("/all-available-to-transfer")
  public List<TransferResponse> getAllAvailableToTransfer() {
    log.info("getAllTransfers request received");
    List<Transfer> transfers = transferService.getAvailableToTransfer();
    return transferMapper.map(transfers);
  }

  @Operation(
      summary = "Get transfer detail",
      description = "Returns all created transfers",
      tags = {"Get"})
  @Parameter(
      name = "transferId",
      description = "Id of the transfer to get its detail",
      required = true)
  @GetMapping("/{transferId}")
  public TransferResponse getTransfer(@PathVariable Long transferId) {
    log.info("getTransfer request received for transferId:{}", transferId);
    return transferMapper.map(transferService.getTransfer(transferId));
  }

  @Operation(
      summary = "Remove transfer",
      description = "Deletes transfer with given id",
      tags = {"Delete"})
  @Parameter(name = "transferId", description = "Id of the transfer to remove", required = true)
  @DeleteMapping("/{transferId}")
  public ResponseEntity<Void> deleteTransfer(@PathVariable Long transferId) {
    log.info("deleteTransfer request received for transferId:{}", transferId);
    transferService.deleteTransfer(transferId);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Add a player into transfer list",
      description =
          "Creates a new transfer with requested player. Other users can transfer this player.",
      tags = {"Post"})
  @PostMapping("/add-player-to-transfer-list")
  public ResponseEntity<AddPlayerToTransferListResponse> addPlayerToTransferList(
      @Valid @RequestBody TransferRequest transferRequest, Principal principal)
      throws JsonProcessingException {

    log.info(
        "addPlayerToTransferList request received for transferRequest:{}",
        objectMapper.writeValueAsString(transferRequest));

    Transfer transfer =
        transferService.createTransfer(
            transferRequest.getPlayerId(), transferRequest.getRequestedPrice(), principal);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new AddPlayerToTransferListResponse(transfer.getId()));
  }

  @Operation(
      summary = "Transfer a player",
      description = "Transfer a player from transfer list.",
      tags = {"Put"})
  @Parameter(
      name = "transferId",
      description = "Id of the transfer to be transferred",
      required = true)
  @PutMapping("/{transferId}/transfer-player")
  public ResponseEntity<Void> transferPlayer(@PathVariable Long transferId, Principal principal) {
    log.info("transferPlayer request received for transferId:{}", transferId);

    transferService.transferPlayer(transferId, principal.getName());
    return ResponseEntity.ok().build();
  }
}
