package com.turgul.soccer.manager.mapper;

import com.turgul.soccer.manager.domain.model.Transfer;
import com.turgul.soccer.manager.dto.response.TransferResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransferMapper {

  @Mapping(source = "player.id", target = "playerId")
  @Mapping(source = "currentTeam.id", target = "currentTeamId")
  @Mapping(source = "previousTeamId.id", target = "previousTeamId")
  List<TransferResponse> map(List<Transfer> transfer);

  @Mapping(source = "player.id", target = "playerId")
  @Mapping(source = "currentTeam.id", target = "currentTeamId")
  TransferResponse map(Transfer transfer);
}
