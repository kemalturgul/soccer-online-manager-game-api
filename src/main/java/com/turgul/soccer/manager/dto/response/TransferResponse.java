package com.turgul.soccer.manager.dto.response;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record TransferResponse(
    Long id,
    Long marketValue,
    Long requestedValue,
    Boolean transferred,
    Long playerId,
    Long currentTeamId)
    implements Serializable {}
