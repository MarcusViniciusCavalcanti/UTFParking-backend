FROM maven:3.6.3-jdk-14 as api
WORKDIR /usr/src/api
COPY pom.xml .
RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
COPY . .
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml package

FROM openjdk:14-jdk
RUN useradd -ms /bin/bash application
WORKDIR /backend

RUN chown application:application /backend
COPY --from=api /usr/src/api/target/utfparking-0.1.1.jar .

ENTRYPOINT ["java", "-jar", "/backend/utfparking-0.1.1.jar"]
CMD ["--spring.profiles.active=docker"]
