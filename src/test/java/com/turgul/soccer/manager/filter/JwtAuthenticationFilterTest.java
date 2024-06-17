package com.turgul.soccer.manager.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.turgul.soccer.manager.domain.Role;
import com.turgul.soccer.manager.domain.model.Users;
import com.turgul.soccer.manager.service.UserDetailService;
import com.turgul.soccer.manager.service.impl.JwtServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
  @InjectMocks private JwtAuthenticationFilter filter;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private UserDetailService userDetailService;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private JwtServiceImpl jwtService;

  @Test
  @SneakyThrows
  public void shouldSetAuthenticationWhenRequestContainsAuthHeaderAndTokenIsValid() {
    HttpServletRequest request = mock(HttpServletRequest.class, RETURNS_DEEP_STUBS);

    when(request.getHeader("Authorization"))
        .thenReturn(
            "Bearer eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbiI6InRydWUifQ.MAYCAQACAQA");
    when(jwtService.extractUserName(anyString())).thenReturn("username");
    when(userDetailService.loadUserByUsername(anyString()))
        .thenReturn(Users.builder().role(Role.USER).build());
    when(jwtService.isTokenValid(anyString(), any(Users.class))).thenReturn(true);

    SecurityContextHolder.getContext().setAuthentication(null);
    filter.doFilterInternal(request, null, mock(FilterChain.class));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication.isAuthenticated()).isTrue();
    assertThat(authentication.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsOnly(Role.USER.name());
  }

  @Test
  @SneakyThrows
  public void shouldNotSetAuthenticationContextWhenNoAuthorizationHeaderInRequest() {
    HttpServletRequest request = mock(HttpServletRequest.class, RETURNS_DEEP_STUBS);
    SecurityContextHolder.getContext().setAuthentication(null);
    filter.doFilterInternal(request, null, mock(FilterChain.class));
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  @SneakyThrows
  public void shouldNotSetAuthenticationContextWhenNoBearerTokenExistsInAuthorizationHeader() {
    HttpServletRequest request = mock(HttpServletRequest.class, RETURNS_DEEP_STUBS);

    when(request.getHeader("Authorization")).thenReturn("Basic c29jY2VyOnBhc3N3b3Jk");
    SecurityContextHolder.getContext().setAuthentication(null);

    filter.doFilterInternal(request, null, mock(FilterChain.class));
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  @SneakyThrows
  public void shouldNotSetAuthenticationContextWhenWrongFormatBearerTokenInAuthorizationHeader() {
    HttpServletRequest request = mock(HttpServletRequest.class, RETURNS_DEEP_STUBS);

    when(request.getHeader("Authorization")).thenReturn("Bearer TestToken");
    SecurityContextHolder.getContext().setAuthentication(null);

    filter.doFilterInternal(request, null, mock(FilterChain.class));
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  @SneakyThrows
  public void shouldNotSetAuthenticationContextWhenRequestHasExpiredAuthorizationToken() {
    HttpServletRequest request = mock(HttpServletRequest.class, RETURNS_DEEP_STUBS);
    HttpServletResponse response = mock(HttpServletResponse.class, RETURNS_DEEP_STUBS);

    when(request.getHeader("Authorization"))
        .thenReturn(
            "Bearer eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbiI6InRydWUifQ.MAYCAQACAQA");
    when(jwtService.extractUserName(anyString()))
        .thenThrow(new ExpiredJwtException(null, null, null));
    SecurityContextHolder.getContext().setAuthentication(null);

    filter.doFilterInternal(request, response, mock(FilterChain.class));
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  @SneakyThrows
  public void shouldNotSetAuthenticationContextWhenRequestBearerTokenIsNotValid() {
    HttpServletRequest request = mock(HttpServletRequest.class, RETURNS_DEEP_STUBS);

    when(request.getHeader("Authorization"))
        .thenReturn(
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
    when(jwtService.extractUserName(anyString())).thenReturn("username");
    when(userDetailService.loadUserByUsername(anyString()))
        .thenReturn(Users.builder().role(Role.USER).build());
    when(jwtService.isTokenValid(anyString(), any(Users.class))).thenReturn(false);

    SecurityContextHolder.getContext().setAuthentication(null);

    filter.doFilterInternal(request, null, mock(FilterChain.class));
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }
}
