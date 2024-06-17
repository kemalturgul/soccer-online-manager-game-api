package com.turgul.soccer.manager.dto.response;

import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record TeamResponse(
    Long id,
    String name,
    String country,
    Long teamValue,
    Long teamCash,
    List<PlayerResponse> players)
    implements Serializable {}
