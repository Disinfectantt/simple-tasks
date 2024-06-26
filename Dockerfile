FROM eclipse-temurin:21-jre-alpine

RUN mkdir /opt/app && \
    addgroup -S simple_tasks && \
    adduser -S simple_tasks -G simple_tasks
COPY ./build/libs/simple-tasks-0.0.1-SNAPSHOT.jar /opt/app/simple-tasks.jar
USER simple_tasks:simple_tasks

CMD ["java", "-jar", "/opt/app/simple-tasks.jar"]
