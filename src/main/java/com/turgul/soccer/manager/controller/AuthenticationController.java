package com.turgul.soccer.manager.controller;

import com.turgul.soccer.manager.dto.request.SignInRequest;
import com.turgul.soccer.manager.dto.request.SignUpRequest;
import com.turgul.soccer.manager.dto.response.JwtAuthenticationResponse;
import com.turgul.soccer.manager.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Authentication management APIs")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @Operation(
      summary = "Create user",
      description = "Creates a user with credentials",
      tags = {"Post"})
  @PostMapping("/signup")
  public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request) {
    authenticationService.signUp(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(
      summary = "Sing in and get generated token",
      description = "Sign in with user credentials and creates token if credentials are correct",
      tags = {"Post"})
  @PostMapping("/signin")
  public ResponseEntity<JwtAuthenticationResponse> signin(
      @Valid @RequestBody SignInRequest request) {
    return ResponseEntity.ok(authenticationService.signIn(request));
  }
}
