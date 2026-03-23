package com.arena.execution.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class DockerConfig {

    @Value("${docker.host:npipe:////./pipe/docker_engine}")
    private String dockerHost;

    // All images required by the execution service
    private static final List<String> REQUIRED_IMAGES = List.of(
            "eclipse-temurin:21-jdk-alpine",
            "python:3.12-slim",
            "node:21-slim"
    );

    @Bean
    public DockerClientConfig dockerClientConfig() {
        return DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();
    }

    @Bean
    public DockerHttpClient dockerHttpClient(DockerClientConfig config) {
        return new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
    }

    @Bean
    public DockerClient dockerClient(DockerClientConfig config, DockerHttpClient httpClient) {
        DockerClient client = DockerClientImpl.getInstance(config, httpClient);

        // Verify Docker daemon is reachable
        try {
            client.pingCmd().exec();
            log.info("Docker client connected successfully to: {}", dockerHost);
        } catch (Exception e) {
            log.error("Failed to connect to Docker: {}", e.getMessage());
            throw new RuntimeException("Docker is not reachable at: " + dockerHost, e);
        }

        // Ensure all required sandbox images are available locally
        pullRequiredImages(client);

        return client;
    }

    /**
     * Pull all required Docker images if they are not already present locally.
     * This runs once at application startup so that the first execution request
     * never fails with a "No such image" NotFoundException.
     */
    private void pullRequiredImages(DockerClient client) {
        for (String image : REQUIRED_IMAGES) {
            try {
                // If inspectImage succeeds the image is already present — skip pull
                client.inspectImageCmd(image).exec();
                log.info("Docker image already present locally: {}", image);
            } catch (NotFoundException e) {
                log.info("Docker image not found locally, pulling: {}", image);
                try {
                    client.pullImageCmd(image)
                            .exec(new PullImageResultCallback())
                            .awaitCompletion(10, TimeUnit.MINUTES);
                    log.info("Successfully pulled Docker image: {}", image);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while pulling Docker image: " + image, ie);
                } catch (Exception pullEx) {
                    // Non-fatal: log and continue — execution will fail gracefully per submission
                    log.error("Failed to pull Docker image: {} — {}", image, pullEx.getMessage());
                }
            } catch (Exception e) {
                log.warn("Could not inspect Docker image: {} — {}", image, e.getMessage());
            }
        }
    }
}