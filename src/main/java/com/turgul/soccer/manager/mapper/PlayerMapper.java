package com.turgul.soccer.manager.mapper;

import com.turgul.soccer.manager.domain.model.Player;
import com.turgul.soccer.manager.dto.response.PlayerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlayerMapper {

  @Mapping(source = "team.id", target = "teamId")
  PlayerResponse map(Player player);
}
