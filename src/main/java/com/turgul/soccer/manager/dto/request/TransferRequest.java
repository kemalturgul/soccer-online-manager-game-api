package com.turgul.soccer.manager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

  @Schema(description = "Player Id", example = "25", required = true)
  @Positive
  private Long playerId;

  @Schema(description = "New price of Player to make transfer", example = "10000", required = true)
  @PositiveOrZero
  private Long requestedPrice;
}
