package com.turgul.soccer.manager.mapper;

import com.turgul.soccer.manager.domain.model.Team;
import com.turgul.soccer.manager.dto.response.TeamResponse;
import org.mapstruct.*;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {

  TeamResponse map(Team team);
}
