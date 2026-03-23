package com.arena.common.constants;

public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // API Versions
    public static final String API_V1 = "/api/v1";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Rating
    public static final int DEFAULT_RATING = 1000;
    public static final int K_FACTOR = 32;
    public static final int RATING_RANGE_INITIAL = 200;
    public static final int RATING_RANGE_EXPANDED = 400;

    // Code Execution
    public static final int DEFAULT_TIME_LIMIT_SECONDS = 5;
    public static final int DEFAULT_MEMORY_LIMIT_MB = 256;
    public static final long CONTAINER_MEMORY_BYTES = 256 * 1024 * 1024L;
    public static final long CONTAINER_CPU_QUOTA = 50000L;

    // Matchmaking
    public static final int MATCHMAKING_TIMEOUT_SECONDS = 60;
    public static final int MATCHMAKING_CHECK_INTERVAL_MS = 2000;

    // Battle
    public static final int BATTLE_COUNTDOWN_SECONDS = 3;
    public static final int MAX_HINT_COUNT = 3;

    // WebSocket
    public static final String WS_ENDPOINT = "/ws/battle";
    public static final String WS_TOPIC_PREFIX = "/topic";
    public static final String WS_APP_PREFIX = "/app";
    public static final String WS_USER_PREFIX = "/user";

    // RabbitMQ Exchanges
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String BATTLE_EXCHANGE = "battle.exchange";
    public static final String CODE_EXCHANGE = "code.exchange";

    // RabbitMQ Queues
    public static final String USER_SYNC_QUEUE = "user.sync.queue";
    public static final String BATTLE_RESULT_QUEUE = "battle.result.queue";
    public static final String CODE_EXECUTION_QUEUE = "code.execution.queue";
    public static final String CODE_RESULT_QUEUE = "code.result.queue";

    // RabbitMQ Routing Keys
    public static final String USER_CREATED_KEY = "user.created";
    public static final String USER_UPDATED_KEY = "user.updated";
    public static final String BATTLE_COMPLETED_KEY = "battle.completed";
    public static final String CODE_EXECUTE_KEY = "code.execute";
    public static final String CODE_RESULT_KEY = "code.result";
}