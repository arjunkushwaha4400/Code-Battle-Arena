package com.arena.battle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.arena.battle", "com.arena.common"})
@EnableScheduling
@EnableDiscoveryClient
public class BattleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BattleServiceApplication.class, args);
    }
}