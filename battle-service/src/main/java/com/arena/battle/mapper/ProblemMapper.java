package com.arena.battle.mapper;

import com.arena.battle.dto.CreateProblemRequest;
import com.arena.battle.entity.Problem;
import com.arena.common.dto.ProblemDTO;
import com.arena.common.dto.TestCaseDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProblemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "testCases", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Problem toEntity(CreateProblemRequest request);

    @Mapping(target = "sampleTestCases", expression = "java(toSampleTestCases(problem))")
    ProblemDTO toDTO(Problem problem);

    List<ProblemDTO> toDTOList(List<Problem> problems);

    default List<TestCaseDTO> toSampleTestCases(Problem problem) {
        if (problem.getTestCases() == null) {
            return List.of();
        }
        return problem.getTestCases().stream()
                .filter(tc -> !tc.getIsHidden())
                .map(tc -> TestCaseDTO.builder()
                        .id(tc.getId())
                        .input(tc.getInput())
                        .expectedOutput(tc.getExpectedOutput())
                        .isHidden(tc.getIsHidden())
                        .orderIndex(tc.getOrderIndex())
                        .build())
                .toList();
    }
}