./mvnw clean package

cp target/*.jar src/main/docker

docker context use default
docker rmi -f simakoff/scrumhelpapp:latest

docker-compose -f src/main/docker/docker-compose.yml build
#docker-compose --env-file src/main/docker/prom.env -f src/main/docker/docker-compose.yml up -d