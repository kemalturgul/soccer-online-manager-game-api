package com.turgul.soccer.manager.controller;

import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;

import com.turgul.soccer.manager.SoccerOnlineManagerGameApiApplication;
import com.turgul.soccer.manager.domain.PlayingPosition;
import com.turgul.soccer.manager.dto.request.SignInRequest;
import com.turgul.soccer.manager.dto.request.SignUpRequest;
import com.turgul.soccer.manager.dto.request.TeamUpdateRequest;
import com.turgul.soccer.manager.dto.request.TransferRequest;
import com.turgul.soccer.manager.dto.response.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SoccerOnlineManagerGameApiApplication.class)
public class UserE2ETest {

  @LocalServerPort private int port;
  @Autowired private TestRestTemplate restTemplate;
  private static String testUserEmail = "testuser@mail.com";

  @Test
  void whenUserIsCreatedThenItsTeamAndPlayersShouldAlsoBeCreated() {
    // Given
    var loginResponse = recreateTestUserAndGetAuthToken();
    HttpHeaders headers = createAuthenticationHeader(loginResponse.getAccessToken());

    // When
    HttpEntity<?> httpEntity = new HttpEntity<>(null, headers);
    TeamResponse teamResponse = getTeamOfUser(httpEntity);

    // Then
    assertThat(teamResponse).isNotNull();
    assertThat(teamResponse.country()).isEqualTo("Spain");
    assertThat(teamResponse.teamValue()).isEqualTo(10_000_000L);
    assertThat(teamResponse.teamCash()).isEqualTo(3_000_000L);
    assertThat(teamResponse.players()).hasSize(20);

    Map<PlayingPosition, List<PlayerResponse>> playerPositionMap =
        teamResponse.players().stream().collect(groupingBy(PlayerResponse::position));
    assertThat(playerPositionMap.get(PlayingPosition.GOALKEEPER)).hasSize(3);
    assertThat(playerPositionMap.get(PlayingPosition.DEFENDER)).hasSize(5);
    assertThat(playerPositionMap.get(PlayingPosition.MIDFIELDER)).hasSize(6);
    assertThat(playerPositionMap.get(PlayingPosition.ATTACKER)).hasSize(6);
  }

  @Test
  void whenUserIsCreatedThenShouldUpdateItsTeamDetail() {
    // Given
    var loginResponse = recreateTestUserAndGetAuthToken();
    HttpHeaders headers = createAuthenticationHeader(loginResponse.getAccessToken());

    HttpEntity<?> httpEntity = new HttpEntity<>(null, headers);
    TeamResponse teamOfUser = getTeamOfUser(httpEntity);

    var teamUpdateRequestBody =
        TeamUpdateRequest.builder()
            .teamId(teamOfUser.id())
            .country("Turkey")
            .name("My Team")
            .teamValue(250000L)
            .build();
    httpEntity = new HttpEntity<>(teamUpdateRequestBody, headers);

    // When
    ResponseEntity<TeamResponse> teamUpdateResponseEntity =
        restTemplate.exchange(
            createRequestUrl("/api/v1/teams/update"),
            HttpMethod.PUT,
            httpEntity,
            TeamResponse.class);

    // Then
    var updatedTeamResponse = teamUpdateResponseEntity.getBody();
    assertThat(updatedTeamResponse).isNotNull();
    assertThat(updatedTeamResponse.country()).isEqualTo("Turkey");
    assertThat(updatedTeamResponse.name()).isEqualTo("My Team");
    assertThat(updatedTeamResponse.teamValue()).isEqualTo(250000L);
    assertThat(updatedTeamResponse.teamCash()).isEqualTo(3000000L);
  }

  @Test
  void putPlayerOnTransferListSuccessfully() {
    // Given
    var loginResponse = recreateTestUserAndGetAuthToken();
    HttpHeaders headers = createAuthenticationHeader(loginResponse.getAccessToken());

    // When
    HttpEntity<?> httpEntity = new HttpEntity<>(null, headers);
    TeamResponse teamResponse = getTeamOfUser(httpEntity);

    var transferReqBody =
        TransferRequest.builder()
            .playerId(teamResponse.players().get(0).id())
            .requestedPrice(10000L)
            .build();

    httpEntity = new HttpEntity<>(transferReqBody, headers);
    ResponseEntity<AddPlayerToTransferListResponse> transferResponseEntity =
        restTemplate.exchange(
            createRequestUrl("/api/v1/transfers/add-player-to-transfer-list"),
            HttpMethod.POST,
            httpEntity,
            AddPlayerToTransferListResponse.class);

    ResponseEntity<TransferResponse[]> transferDetailResponse =
        restTemplate.exchange(
            createRequestUrl("/api/v1/transfers/all"),
            HttpMethod.GET,
            httpEntity,
            TransferResponse[].class);

    // Then
    assertThat(transferResponseEntity).isNotNull();
    assertThat(transferResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(transferResponseEntity.getBody().transferId()).isGreaterThan(0L);

    assertThat(transferDetailResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(transferDetailResponse.getBody()).isNotNull();
    assertThat(transferDetailResponse.getBody().length).isEqualTo(1);
  }

  @Test
  void transferAPlayerFromTransferListSuccessfully() {
    // Given
    var loginResponse = recreateTestUserAndGetAuthToken();
    HttpHeaders headers = createAuthenticationHeader(loginResponse.getAccessToken());

    // When
    HttpEntity<?> httpEntity = new HttpEntity<>(null, headers);
    TeamResponse teamResponse = getTeamOfUser(httpEntity);

    var transferReqBody =
        TransferRequest.builder()
            .playerId(teamResponse.players().get(0).id())
            .requestedPrice(10_000L)
            .build();
    httpEntity = new HttpEntity<>(transferReqBody, headers);
    ResponseEntity<AddPlayerToTransferListResponse> transferResponseEntity =
        restTemplate.exchange(
            createRequestUrl("/api/v1/transfers/add-player-to-transfer-list"),
            HttpMethod.POST,
            httpEntity,
            AddPlayerToTransferListResponse.class);

    // Then
    assertThat(transferResponseEntity).isNotNull();
    assertThat(transferResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(transferResponseEntity.getBody().transferId()).isGreaterThan(0L);

    // Created another user to transfer a player
    removeUser("user2@mail.com");
    createUser("user2@mail.com");
    var loginResponse2 = loginUser("user2@mail.com");
    var headers2 = createAuthenticationHeader(loginResponse2.getAccessToken());

    var httpEntity2 = new HttpEntity<>(null, headers2);
    restTemplate.exchange(
        createRequestUrl(
            String.format(
                "/api/v1/transfers/%s/transfer-player",
                transferResponseEntity.getBody().transferId())),
        HttpMethod.PUT,
        httpEntity2,
        String.class);

    var transferResponse = getTransfer(httpEntity2, transferResponseEntity.getBody().transferId());
    var previousTeamOfPlayer = getTeamOfUser(httpEntity);
    var newTeamOfPlayer = getTeamOfUser(httpEntity2);

    assertThat(transferResponse.transferred()).isTrue();
    assertThat(transferResponse.currentTeamId()).isEqualTo(newTeamOfPlayer.id());
    assertThat(newTeamOfPlayer.players()).hasSize(21);
    assertThat(newTeamOfPlayer.teamCash()).isEqualTo(2_990_000);
    assertThat(newTeamOfPlayer.teamValue()).isEqualTo(10_500_000L);

    assertThat(previousTeamOfPlayer.players()).hasSize(19);
    assertThat(previousTeamOfPlayer.teamCash()).isEqualTo(3_010_000);
    assertThat(previousTeamOfPlayer.teamValue()).isEqualTo(9_500_000L);
  }

  @Test
  void whenTeamHasInsufficientBudgetForTransferringThenReturnsBadRequestStatusCode() {
    // Given
    var loginResponse = recreateTestUserAndGetAuthToken();
    HttpHeaders headers = createAuthenticationHeader(loginResponse.getAccessToken());

    // When
    HttpEntity<?> httpEntity = new HttpEntity<>(null, headers);
    TeamResponse teamResponse = getTeamOfUser(httpEntity);

    var transferPlayerId = teamResponse.players().get(0).id();
    var transferReqBody =
        TransferRequest.builder().playerId(transferPlayerId).requestedPrice(3_500_000L).build();

    httpEntity = new HttpEntity<>(transferReqBody, headers);
    ResponseEntity<AddPlayerToTransferListResponse> transferResponseEntity =
        restTemplate.exchange(
            createRequestUrl("/api/v1/transfers/add-player-to-transfer-list"),
            HttpMethod.POST,
            httpEntity,
            AddPlayerToTransferListResponse.class);

    // Then
    assertThat(transferResponseEntity).isNotNull();
    assertThat(transferResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(transferResponseEntity.getBody().transferId()).isGreaterThan(0L);

    // Created another user to transfer a player
    createUser("user2@mail.com");
    var loginResponse2 = loginUser("user2@mail.com");
    var headers2 = createAuthenticationHeader(loginResponse2.getAccessToken());

    var httpEntity2 = new HttpEntity<>(null, headers2);
    var failedTransfer =
        restTemplate.exchange(
            createRequestUrl(
                String.format(
                    "/api/v1/transfers/%s/transfer-player",
                    transferResponseEntity.getBody().transferId())),
            HttpMethod.PUT,
            httpEntity2,
            ErrorBody.class);
    assertThat(failedTransfer).isNotNull();
    assertThat(failedTransfer.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(failedTransfer.getBody().message())
        .isEqualTo(
            "400 BAD_REQUEST \"Insufficient budget to transfer this player, teamCash:3000000\"");

    var transferResponse = getTransfer(httpEntity2, transferResponseEntity.getBody().transferId());
    var currentTeamOfPlayer = getTeamOfUser(httpEntity);

    assertThat(transferResponse.transferred()).isFalse();
    assertThat(transferResponse.currentTeamId()).isEqualTo(currentTeamOfPlayer.id());

    assertThat(currentTeamOfPlayer.players()).hasSize(20);
    assertThat(currentTeamOfPlayer.teamCash()).isEqualTo(3_000_000);
    assertThat(currentTeamOfPlayer.teamValue()).isEqualTo(10_000_000L);
  }

  private JwtAuthenticationResponse loginUser(String userEmail) {
    // Given
    var requestBody = SignInRequest.builder().email(userEmail).password("secret").build();

    // When
    var response =
        restTemplate.postForEntity(
            createRequestUrl("/api/v1/auth/signin"), requestBody, JwtAuthenticationResponse.class);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    return response.getBody();
  }

  private HttpHeaders createAuthenticationHeader(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    return headers;
  }

  private void createUser(String userEmail) {
    var requestBody = createSignUpRequestBody(userEmail);
    var response =
        restTemplate.postForEntity(
            createRequestUrl("/api/v1/auth/signup"), requestBody, String.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  private void removeUser(String userEmail) {
    restTemplate.delete(createRequestUrl("/api/v1/auth/remove-user/" + userEmail));
  }

  private TeamResponse getTeamOfUser(HttpEntity<?> httpEntity) {
    var teamResponseEntity =
        restTemplate.exchange(
            createRequestUrl("/api/v1/teams/team-of-user"),
            HttpMethod.GET,
            httpEntity,
            TeamResponse.class);
    assertThat(teamResponseEntity).isNotNull();
    assertThat(teamResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(teamResponseEntity.getBody()).isNotNull();

    return teamResponseEntity.getBody();
  }

  private TeamResponse getTeam(HttpEntity<?> httpEntity, Long teamId) {
    var teamResponseEntity =
        restTemplate.exchange(
            createRequestUrl(String.format("/api/v1/teams/%s", teamId)),
            HttpMethod.GET,
            httpEntity,
            TeamResponse.class);

    assertThat(teamResponseEntity).isNotNull();
    assertThat(teamResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(teamResponseEntity.getBody()).isNotNull();

    return teamResponseEntity.getBody();
  }

  private TransferResponse getTransfer(HttpEntity<?> httpEntity, Long transferId) {
    ResponseEntity<TransferResponse> transferResponseEntity =
        restTemplate.exchange(
            createRequestUrl("/api/v1/transfers/" + transferId),
            HttpMethod.GET,
            httpEntity,
            TransferResponse.class);

    assertThat(transferResponseEntity).isNotNull();
    assertThat(transferResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(transferResponseEntity.getBody()).isNotNull();

    return transferResponseEntity.getBody();
  }

  private static SignUpRequest createSignUpRequestBody(String userEmail) {
    return SignUpRequest.builder()
        .firstName("Test")
        .lastName("User")
        .email(userEmail)
        .password("secret")
        .build();
  }

  private JwtAuthenticationResponse recreateTestUserAndGetAuthToken() {
    removeUser(testUserEmail);
    createUser(testUserEmail);
    return loginUser(testUserEmail);
  }

  private String createRequestUrl(String uri) {
    return String.format("http://localhost:%d%s", port, uri);
  }
}
