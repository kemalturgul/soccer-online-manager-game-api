package com.turgul.soccer.manager.controller;

import com.turgul.soccer.manager.dto.request.SignInRequest;
import com.turgul.soccer.manager.dto.request.SignUpRequest;
import com.turgul.soccer.manager.dto.response.JwtAuthenticationResponse;
import com.turgul.soccer.manager.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
    log.debug("signup request received for userId:{}", request.getEmail());
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
    log.debug("signin request received for userId:{}", request.getEmail());
    return ResponseEntity.ok(authenticationService.signIn(request));
  }

  @Operation(
      summary = "Remove user",
      description = "Deletes a user and its team from the system",
      tags = {"Delete"})
  // @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping("/remove-user/{userEmail}")
  public ResponseEntity<Void> deleteUser(@NotBlank @PathVariable String userEmail) {
    log.info("deleteUser request received for userEmail:{}", userEmail);
    authenticationService.deleteUser(userEmail);
    return ResponseEntity.ok().build();
  }
}
