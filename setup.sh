#!/bin/bash

echo "starting docker"
docker compose up -d
echo "starting server"
export $(cat .env | xargs) && ./mvnw spring-boot:run -Dmaven.test.skip=true
echo "backend setup completed"
