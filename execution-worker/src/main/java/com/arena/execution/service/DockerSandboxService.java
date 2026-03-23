package com.arena.execution.service;

import com.arena.common.enums.ProgrammingLanguage;
import com.arena.execution.dto.ExecutionContext;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.command.PullImageResultCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerSandboxService {

    private final DockerClient dockerClient;

    @Value("${execution.work-dir:C:/arena-execution}")
    private String baseWorkDir;

    @Value("${execution.container-memory:268435456}")
    private long containerMemory;

    @Value("${execution.container-cpu-quota:50000}")
    private long containerCpuQuota;


    public String createContainer(ExecutionContext context) {
        log.debug("Creating container for submission: {}", context.getSubmissionId());

        String image = getDockerImage(context.getLanguage());
        pullImageIfMissing(image);

        // Create host work directory
        String workDir = baseWorkDir + "/" + context.getSubmissionId().toString();
        try {
            Files.createDirectories(Path.of(workDir));
            context.setWorkDir(workDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create work directory: " + workDir, e);
        }

        String dockerBindPath = toDockerPath(workDir);
        log.debug("Bind mount host path: {} → /code inside container", dockerBindPath);

        HostConfig hostConfig = HostConfig.newHostConfig()
                .withMemory(containerMemory)
                .withMemorySwap(containerMemory)
                .withCpuQuota(containerCpuQuota)
                .withNetworkMode("none")
                .withReadonlyRootfs(false)
                .withBinds(new Bind(dockerBindPath, new Volume("/code")))
                .withAutoRemove(false);

        CreateContainerResponse container = dockerClient.createContainerCmd(image)
                .withHostConfig(hostConfig)
                .withWorkingDir("/code")
                .withUser("root")
                .withCmd("tail", "-f", "/dev/null")
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();

        log.info("Created container: {} for submission: {}",
                container.getId().substring(0, 12), context.getSubmissionId());

        return container.getId();
    }

    public void copyCodeToContainer(ExecutionContext context) {
        log.debug("Copying code to container: {}", context.getContainerId().substring(0, 12));

        String fileName = getFileName(context.getLanguage());
        String filePath = context.getWorkDir() + "/" + fileName;

        try {
            Files.writeString(Path.of(filePath), context.getCode(), StandardCharsets.UTF_8);

            // Verify file was actually written — guards against silent bind-mount failures
            if (!Files.exists(Path.of(filePath))) {
                throw new RuntimeException("Code file was not created at: " + filePath);
            }
            log.debug("Code written and verified: {} ({} bytes)",
                    filePath, Files.size(Path.of(filePath)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write code file: " + filePath, e);
        }
    }

    public CodeExecutionService.CompilationResult compileCode(ExecutionContext context) {
        if (context.getLanguage() != ProgrammingLanguage.JAVA) {
            return new CodeExecutionService.CompilationResult(true, null);
        }

        log.debug("Compiling Java code in container: {}", context.getContainerId().substring(0, 12));


        try {
            Thread.sleep(300);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        try {
            // Debug: log what the container actually sees in /code
            logContainerDirectory(context.getContainerId(), "/code");

            // Compile using absolute path to be safe
            ExecCreateCmdResponse execCreate = dockerClient.execCreateCmd(context.getContainerId())
                    .withCmd("javac", "/code/Main.java")
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();

            dockerClient.execStartCmd(execCreate.getId())
                    .exec(new ExecResultCallback(stdout, stderr))
                    .awaitCompletion(30, TimeUnit.SECONDS);

            String stderrOutput = stderr.toString(StandardCharsets.UTF_8);

            if (!stderrOutput.isEmpty()) {
                log.warn("Compilation error for submission {}: {}",
                        context.getSubmissionId(), stderrOutput);
                return new CodeExecutionService.CompilationResult(false, stderrOutput);
            }

            return new CodeExecutionService.CompilationResult(true, null);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CodeExecutionService.CompilationResult(false, "Compilation timed out");
        }
    }

    public ExecutionOutput executeCode(ExecutionContext context, String input, int timeoutSeconds) {
        log.debug("Executing code in container: {}", context.getContainerId().substring(0, 12));

        String[] command = getExecuteCommand(context.getLanguage());

        try {
            String inputFile = context.getWorkDir() + "/input.txt";
            Files.writeString(Path.of(inputFile),
                    input != null ? input : "", StandardCharsets.UTF_8);

            String fullCommand = String.join(" ", command) + " < /code/input.txt";

            ExecCreateCmdResponse execCreate = dockerClient.execCreateCmd(context.getContainerId())
                    .withCmd("sh", "-c", fullCommand)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();

            long startTime = System.currentTimeMillis();

            boolean completed = dockerClient.execStartCmd(execCreate.getId())
                    .exec(new ExecResultCallback(stdout, stderr))
                    .awaitCompletion(timeoutSeconds, TimeUnit.SECONDS);

            long executionTime = System.currentTimeMillis() - startTime;

            if (!completed) {
                return new ExecutionOutput("", "Time Limit Exceeded", (int) executionTime, true);
            }

            String output = stdout.toString(StandardCharsets.UTF_8).trim();
            String error  = stderr.toString(StandardCharsets.UTF_8).trim();

            return new ExecutionOutput(
                    output,
                    error.isEmpty() ? null : error,
                    (int) executionTime,
                    false
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ExecutionOutput("", "Execution interrupted", 0, true);
        } catch (IOException e) {
            return new ExecutionOutput("", "IO Error: " + e.getMessage(), 0, true);
        }
    }

    public void removeContainer(String containerId) {
        log.debug("Removing container: {}", containerId.substring(0, 12));

        try {
            dockerClient.stopContainerCmd(containerId).withTimeout(2).exec();
        } catch (Exception e) {
            log.debug("Container already stopped: {}", e.getMessage());
        }

        try {
            dockerClient.removeContainerCmd(containerId)
                    .withForce(true)
                    .withRemoveVolumes(true)
                    .exec();
            log.info("Removed container: {}", containerId.substring(0, 12));
        } catch (Exception e) {
            log.warn("Failed to remove container {}: {}",
                    containerId.substring(0, 12), e.getMessage());
        }
    }


    private void pullImageIfMissing(String image) {
        try {
            dockerClient.inspectImageCmd(image).exec();
            log.debug("Image already available locally: {}", image);
        } catch (NotFoundException e) {
            log.warn("Image '{}' not found locally — pulling now.", image);
            try {
                dockerClient.pullImageCmd(image)
                        .exec(new PullImageResultCallback())
                        .awaitCompletion(10, TimeUnit.MINUTES);
                log.info("Successfully pulled image: {}", image);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while pulling image: " + image, ie);
            } catch (Exception pullEx) {
                throw new RuntimeException(
                        "Failed to pull Docker image '" + image + "': " + pullEx.getMessage(), pullEx);
            }
        }
    }

    private void logContainerDirectory(String containerId, String dir) {
        try {
            ExecCreateCmdResponse lsExec = dockerClient.execCreateCmd(containerId)
                    .withCmd("ls", "-la", dir)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStream err = new ByteArrayOutputStream();

            dockerClient.execStartCmd(lsExec.getId())
                    .exec(new ExecResultCallback(out, err))
                    .awaitCompletion(5, TimeUnit.SECONDS);

            log.debug("Container {} directory listing for '{}':\n{}",
                    containerId.substring(0, 12), dir, out.toString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.debug("Could not list container directory {}: {}", dir, e.getMessage());
        }
    }

    private String toDockerPath(String hostPath) {
        return hostPath.replace('\\', '/');
    }

    private String getDockerImage(ProgrammingLanguage language) {
        return switch (language) {
            case JAVA       -> "eclipse-temurin:21-jdk-alpine";
            case PYTHON     -> "python:3.12-slim";
            case JAVASCRIPT -> "node:21-slim";
        };
    }

    private String getFileName(ProgrammingLanguage language) {
        return switch (language) {
            case JAVA       -> "Main.java";
            case PYTHON     -> "main.py";
            case JAVASCRIPT -> "main.js";
        };
    }

    private String[] getExecuteCommand(ProgrammingLanguage language) {
        return switch (language) {
            case JAVA       -> new String[]{"java", "-cp", "/code", "Main"};
            case PYTHON     -> new String[]{"python3", "/code/main.py"};
            case JAVASCRIPT -> new String[]{"node", "/code/main.js"};
        };
    }



    public record ExecutionOutput(
            String output,
            String error,
            int executionTimeMs,
            boolean timedOut
    ) {}

    private static class ExecResultCallback
            extends com.github.dockerjava.api.async.ResultCallbackTemplate<
            ExecResultCallback, com.github.dockerjava.api.model.Frame> {

        private final OutputStream stdout;
        private final OutputStream stderr;

        ExecResultCallback(OutputStream stdout, OutputStream stderr) {
            this.stdout = stdout;
            this.stderr = stderr;
        }

        @Override
        public void onNext(com.github.dockerjava.api.model.Frame frame) {
            try {
                switch (frame.getStreamType()) {
                    case STDOUT -> stdout.write(frame.getPayload());
                    case STDERR -> stderr.write(frame.getPayload());
                    default     -> {}
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to write exec frame payload", e);
            }
        }
    }
}