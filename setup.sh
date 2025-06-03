#!/bin/bash

echo "starting docker"
docker compose up -d
echo "starting server"
./mvnw spring-boot:run
echo "backend setup completed successfully"
