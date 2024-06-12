package com.turgul.soccer.manager.repository;

import com.turgul.soccer.manager.domain.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

  @Modifying
  @Query("update Team t set t.teamValue=:teamValue, t.teamCash=:teamCash where t.id=:id")
  void updateTeamCashAndValue(
      @Param("teamValue") Long teamValue, @Param("teamCash") Long teamCash, @Param("id") Long id);
}
