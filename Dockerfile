FROM docker/compose:latest
WORKDIR /workspace
COPY docker-compose.yml .
COPY . .
ENTRYPOINT ["docker-compose", "up", "--build"]
