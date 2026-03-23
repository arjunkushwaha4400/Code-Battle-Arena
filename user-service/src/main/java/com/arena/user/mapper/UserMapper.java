package com.arena.user.mapper;

import com.arena.common.dto.LeaderboardEntryDTO;
import com.arena.common.dto.UserDTO;
import com.arena.user.dto.UserProfileDTO;
import com.arena.user.dto.UserStatisticsDTO;
import com.arena.user.entity.User;
import com.arena.user.entity.UserStatistics;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "winRate", expression = "java(user.getWinRate())")
    @Mapping(target = "statistics", source = "statistics")
    UserProfileDTO toProfileDTO(User user);

    UserDTO toDTO(User user);

    List<UserDTO> toDTOList(List<User> users);

    UserStatisticsDTO toStatisticsDTO(UserStatistics statistics);

    @Mapping(target = "rank", ignore = true)
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "winRate", expression = "java(user.getWinRate())")
    LeaderboardEntryDTO toLeaderboardEntry(User user);

    List<LeaderboardEntryDTO> toLeaderboardEntryList(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDTO(UserDTO dto, @MappingTarget User user);
}