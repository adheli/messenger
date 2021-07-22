FROM openjdk:11
WORKDIR /app
COPY target/messenger-0.0.1-SNAPSHOT.jar .
EXPOSE 8084
RUN export spring_profiles_active=docker
ENTRYPOINT [ "java", "-jar", "messenger-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]
