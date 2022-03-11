#!/bin/bash
pushd ../
echo 'Building event sourcing libraries'
./gradlew :codelity-event-sourcing-common:clean :codelity-event-sourcing-common:publishToMavenLocal
./gradlew :codelity-event-sourcing-core:clean :codelity-event-sourcing-core:publishToMavenLocal
./gradlew :codelity-event-sourcing-spring-config:clean :codelity-event-sourcing-spring-config:publishToMavenLocal
./gradlew :codelity-jdbc-eventstore:clean :codelity-jdbc-eventstore:publishToMavenLocal
./gradlew :codelity-jdbc-eventstore-spring-config:clean :codelity-jdbc-eventstore-spring-config:publishToMavenLocal
popd || exit

echo 'Building inventory service'
./gradlew :inventory-service:clean :inventory-service:bootJar

docker-compose up -d