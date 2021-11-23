docker login
docker compose -f src/main/docker/docker-compose.yml push
docker context use myecscontext1
docker compose -f src/main/docker/docker-compose.yml up