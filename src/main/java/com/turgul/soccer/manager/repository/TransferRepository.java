package com.turgul.soccer.manager.repository;

import com.turgul.soccer.manager.domain.model.Team;
import com.turgul.soccer.manager.domain.model.Transfer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

  Optional<Transfer> findByPlayerIdAndTransferredFalse(Long playerId);

  Optional<Transfer> findByIdAndTransferredFalse(Long transferId);

  List<Transfer> findAllByTransferredFalse();

  //  @Query("select t.* from Transfer t where t.transferred=:transferred")
  // List<Transfer> findAllByTransferredFalse(@Param("transferred") Boolean transferred);

  @Modifying
  @Query(
      "update Transfer t set t.currentTeam=:currentTeam ,t.transferred=:transferred where t.id=:id")
  void updateTransfer(
      @Param("currentTeam") Team currentTeam,
      @Param("transferred") Boolean transferred,
      @Param("id") Long id);
}
