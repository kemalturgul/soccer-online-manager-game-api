package com.turgul.soccer.manager.dto.response;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse implements Serializable {
  private String accessToken;
  private int expiresIn;
  private String tokenType;
}
