package com.turgul.soccer.manager.service;

import com.turgul.soccer.manager.dto.request.SignInRequest;
import com.turgul.soccer.manager.dto.request.SignUpRequest;
import com.turgul.soccer.manager.dto.response.JwtAuthenticationResponse;

public interface AuthenticationService {

  void signUp(SignUpRequest request);

  JwtAuthenticationResponse signIn(SignInRequest request);
}
