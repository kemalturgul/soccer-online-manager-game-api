package com.turgul.soccer.manager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
  @Schema(description = "User's first name", example = "Kemal", required = true)
  @NotBlank
  private String firstName;

  @Schema(description = "User's last name", example = "Turgul", required = true)
  @NotBlank
  private String lastName;

  @Schema(description = "User's email", example = "test@gmail.com", required = true)
  @Email
  @NotBlank
  private String email;

  @Schema(
      description = "User's password to use when login",
      example = "stronGPassw0rd",
      required = true)
  @NotBlank
  private String password;
}
