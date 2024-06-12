package com.turgul.soccer.manager.dto.response;

import com.turgul.soccer.manager.domain.PlayingPosition;
import java.io.Serializable;
import lombok.Builder;

@Builder
public record PlayerResponse(
    Long id,
    String firstName,
    String lastName,
    Integer age,
    Long marketValue,
    PlayingPosition position,
    Long teamId)
    implements Serializable {}
