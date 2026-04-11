package com.arena.execution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.arena.execution", "com.arena.common"})
@EnableDiscoveryClient
public class ExecutionWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExecutionWorkerApplication.class, args);
    }
}