package com.arena.user.service;

import com.arena.common.dto.LeaderboardEntryDTO;
import com.arena.common.dto.PagedResponse;
import com.arena.user.entity.User;
import com.arena.user.mapper.UserMapper;
import com.arena.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
//    @Cacheable(value = "leaderboard_page", key = "'rating_' + #page + '-' + #size")
    public PagedResponse<LeaderboardEntryDTO> getLeaderboardByRating(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findTopByRating(pageable);

        AtomicInteger rank = new AtomicInteger(page * size + 1);

        List<LeaderboardEntryDTO> entries = usersPage.getContent().stream()
                .map(user -> {
                    LeaderboardEntryDTO entry = userMapper.toLeaderboardEntry(user);
                    entry.setRank(rank.getAndIncrement());
                    return entry;
                })
                .toList();

        return PagedResponse.of(
                entries,
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
//    @Cacheable(value = "leaderboard_page", key = "'wins_' + #page + '-' + #size")
    public PagedResponse<LeaderboardEntryDTO> getLeaderboardByWins(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findTopByWins(pageable);

        AtomicInteger rank = new AtomicInteger(page * size + 1);

        List<LeaderboardEntryDTO> entries = usersPage.getContent().stream()
                .map(user -> {
                    LeaderboardEntryDTO entry = userMapper.toLeaderboardEntry(user);
                    entry.setRank(rank.getAndIncrement());
                    return entry;
                })
                .toList();

        return PagedResponse.of(
                entries,
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
//    @Cacheable(value = "leaderboard_top", key = "#count")
    public List<LeaderboardEntryDTO> getTopPlayers(int count) {

        Pageable pageable = PageRequest.of(0, count);
        Page<User> usersPage = userRepository.findTopByRating(pageable);

        AtomicInteger rank = new AtomicInteger(1);

        return usersPage.getContent().stream()
                .map(user -> {
                    LeaderboardEntryDTO entry = userMapper.toLeaderboardEntry(user);
                    entry.setRank(rank.getAndIncrement());
                    return entry;
                })
                .toList();
    }
}