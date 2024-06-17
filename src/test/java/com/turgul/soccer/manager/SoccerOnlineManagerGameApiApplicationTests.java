package com.turgul.soccer.manager;

import static org.assertj.core.api.Assertions.assertThat;

import com.turgul.soccer.manager.controller.AuthenticationController;
import com.turgul.soccer.manager.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SoccerOnlineManagerGameApiApplicationTests {

  @Autowired private AuthenticationController authenticationController;

  @Autowired private TeamRepository teamRepository;

  @Test
  void contextLoads() {
    assertThat(authenticationController).isNotNull();
    assertThat(teamRepository).isNotNull();
  }
}
