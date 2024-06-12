package com.turgul.soccer.manager.repository;

import com.turgul.soccer.manager.domain.model.Player;
import com.turgul.soccer.manager.domain.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
  @Modifying
  @Query("update Player p set p.team=:team, p.marketValue=:marketValue where p.id=:id")
  void updatePlayer(
      @Param("team") Team team, @Param("marketValue") Long marketValue, @Param("id") Long id);
}
