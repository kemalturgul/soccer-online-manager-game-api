package com.turgul.soccer.manager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record TeamUpdateRequest(
    @Schema(description = "Team Id", example = "12", required = true) @NotNull Long teamId,
    @Schema(description = "Team Name", example = "Team of Superstars", required = true) @NotNull
        String name,
    @Schema(description = "Country Name of Team", example = "Turkey", required = true) @NotNull
        String country,
    @Schema(description = "Value of Team", example = "150000", required = true) @NotNull
        Long teamValue) {}
