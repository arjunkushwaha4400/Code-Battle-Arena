package com.arena.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProgrammingLanguage {
    JAVA("java", "eclipse-temurin:21-jdk-alpine", ".java", "javac Main.java && java Main"),
    PYTHON("python", "python:3.12-slim", ".py", "python main.py"),
    JAVASCRIPT("javascript", "node:21-slim", ".js", "node main.js");

    private final String name;
    private final String dockerImage;
    private final String fileExtension;
    private final String executeCommand;
}