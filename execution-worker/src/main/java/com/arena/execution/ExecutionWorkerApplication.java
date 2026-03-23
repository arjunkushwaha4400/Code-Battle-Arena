package com.arena.execution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.arena.execution", "com.arena.common"})
public class ExecutionWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExecutionWorkerApplication.class, args);
    }
}