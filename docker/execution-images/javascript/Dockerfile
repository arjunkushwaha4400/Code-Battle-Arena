FROM node:21-slim

# Create non-root user for security
RUN groupadd -r executor && useradd -r -g executor executor

# Create working directory
WORKDIR /code

# Switch to non-root user
USER executor

# Default command
CMD ["sh"]