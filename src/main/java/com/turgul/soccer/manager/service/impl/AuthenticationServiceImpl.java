package com.turgul.soccer.manager.service.impl;

import static com.turgul.soccer.manager.Constants.JWT_TOKEN_VALIDITY_IN_MILLIS;
import static com.turgul.soccer.manager.Constants.TOKEN_TYPE;

import com.turgul.soccer.manager.domain.Role;
import com.turgul.soccer.manager.domain.model.Users;
import com.turgul.soccer.manager.dto.request.SignInRequest;
import com.turgul.soccer.manager.dto.request.SignUpRequest;
import com.turgul.soccer.manager.dto.response.JwtAuthenticationResponse;
import com.turgul.soccer.manager.repository.UserRepository;
import com.turgul.soccer.manager.service.AuthenticationService;
import com.turgul.soccer.manager.service.JwtService;
import com.turgul.soccer.manager.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserRepository userRepository;
  private final TeamService teamService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Override
  public void signUp(SignUpRequest request) {
    userRepository
        .findByEmail(request.getEmail())
        .ifPresent(
            user -> {
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exist!");
            });
    var userEntity = createUserEntity(request);
    var team = teamService.createTeam(userEntity);
    userEntity.setTeam(team);

    userRepository.save(userEntity);
  }

  @Override
  public JwtAuthenticationResponse signIn(SignInRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    var user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password."));
    var jwt = jwtService.generateToken(user);
    return JwtAuthenticationResponse.builder()
        .accessToken(jwt)
        .expiresIn(JWT_TOKEN_VALIDITY_IN_MILLIS)
        .tokenType(TOKEN_TYPE)
        .build();
  }

  private Users createUserEntity(SignUpRequest request) {
    return Users.builder()
        .role(Role.USER)
        .email(request.getEmail())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .password(passwordEncoder.encode(request.getPassword()))
        .build();
  }
}
