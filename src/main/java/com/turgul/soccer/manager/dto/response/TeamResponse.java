package com.turgul.soccer.manager.dto.response;

import java.io.Serializable;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record TeamResponse(String name, String country, Long teamValue, Long teamCash)
    implements Serializable {}
