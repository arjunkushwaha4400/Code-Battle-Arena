package com.arena.battle.mapper;

import com.arena.battle.dto.SubmissionResponse;
import com.arena.battle.entity.Submission;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubmissionMapper {

    @Mapping(target = "roomId", source = "room.id")
    SubmissionResponse toResponse(Submission submission);

    List<SubmissionResponse> toResponseList(List<Submission> submissions);
}