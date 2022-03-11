FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.11_9
WORKDIR /opt/app
ARG APP_JAR=./inventory-service/build/libs/inventory-service-*.jar
COPY ${APP_JAR} ./app.jar
ENTRYPOINT ["java","-jar","app.jar"]