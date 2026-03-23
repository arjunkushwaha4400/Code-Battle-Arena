FROM eclipse-temurin:21-jdk-alpine

# Create non-root user for security
RUN addgroup -S executor && adduser -S executor -G executor

# Create working directory
WORKDIR /code

# Set resource limits
ENV JAVA_OPTS="-Xmx256m -Xms64m"

# Switch to non-root user
USER executor

# Default command
CMD ["sh"]