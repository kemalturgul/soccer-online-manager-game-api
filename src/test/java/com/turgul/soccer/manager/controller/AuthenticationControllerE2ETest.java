package com.turgul.soccer.manager.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.turgul.soccer.manager.SoccerOnlineManagerGameApiApplication;
import com.turgul.soccer.manager.dto.request.SignInRequest;
import com.turgul.soccer.manager.dto.request.SignUpRequest;
import com.turgul.soccer.manager.dto.response.ErrorBody;
import com.turgul.soccer.manager.dto.response.JwtAuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SoccerOnlineManagerGameApiApplication.class)
public class AuthenticationControllerE2ETest {

  @LocalServerPort private int port;
  @Autowired private TestRestTemplate restTemplate;

  private static String testUserEmail = "user1@mail.com";

  @Test
  void signupUserSuccessfully() {
    // Given
    removeUser(testUserEmail);

    // When // Then
    createUser(testUserEmail);
  }

  @Test
  void whenUserAlreadyExistsThenReturnsBadRequestStatusCode() {
    // Given
    removeUser(testUserEmail);
    createUser(testUserEmail);
    var requestBody = createSignUpRequestBody(testUserEmail);

    // When
    var response =
        restTemplate.postForEntity(
            createRequestUrl("/api/v1/auth/signup"), requestBody, ErrorBody.class);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().message()).isEqualTo("400 BAD_REQUEST \"User already exist!\"");
  }

  @Test
  void loginUserSuccessfully() {
    // Given
    removeUser(testUserEmail);
    createUser(testUserEmail);
    var requestBody = SignInRequest.builder().email(testUserEmail).password("password").build();

    // When
    var response =
        restTemplate.postForEntity(
            createRequestUrl("/api/v1/auth/signin"), requestBody, JwtAuthenticationResponse.class);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getAccessToken()).isNotBlank();
    assertThat(response.getBody().getTokenType()).isEqualTo("Bearer");
    assertThat(response.getBody().getExpiresIn()).isEqualTo(3600000);
  }

  @Test
  void whenUserDoesNotExistThenReturnsNotFoundStatusCode() {
    // Given
    var requestBody = createSignUpRequestBody(testUserEmail);
    requestBody.setEmail("other@email.com");

    // When
    var response =
        restTemplate.postForEntity(
            createRequestUrl("/api/v1/auth/signin"), requestBody, ErrorBody.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().message())
        .isEqualTo("404 NOT_FOUND \"User not found:other@email.com\"");
  }

  @Test
  void whenUserPasswordIsWrongThenReturnsForbiddenStatusCode() {
    // Given
    removeUser(testUserEmail);
    createUser(testUserEmail);
    var requestBody = createSignUpRequestBody(testUserEmail);
    requestBody.setPassword("secret");

    // When
    var response =
        restTemplate.postForEntity(
            createRequestUrl("/api/v1/auth/signin"), requestBody, ErrorBody.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void whenRequiredInputsAreNotValidThenReturnsBadRequestStatusCode() {
    // Given
    removeUser(testUserEmail);
    var requestBody = SignUpRequest.builder().build();

    // When
    var response =
        restTemplate.postForEntity(
            createRequestUrl("/api/v1/auth/signin"), requestBody, String.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  void createUser(String userEmail) {
    var requestBody = createSignUpRequestBody(userEmail);
    var response =
        restTemplate.postForEntity(
            createRequestUrl("/api/v1/auth/signup"), requestBody, String.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  void removeUser(String userEmail) {
    restTemplate.delete(createRequestUrl("/api/v1/auth/remove-user/" + userEmail));
  }

  private SignUpRequest createSignUpRequestBody(String userEmail) {
    return SignUpRequest.builder()
        .firstName("Test")
        .lastName("User")
        .email(userEmail)
        .password("password")
        .build();
  }

  private String createRequestUrl(String uri) {
    return String.format("http://localhost:%d%s", port, uri);
  }
}
