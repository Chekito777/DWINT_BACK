FROM docker/compose:latest
WORKDIR /workspace
COPY docker-compose.yml .
COPY . .
ENTRYPOINT ["sh", "-c", "docker-compose up --build --abort-on-container-exit"]
