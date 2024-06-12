package com.turgul.soccer.manager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
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
