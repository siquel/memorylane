FROM maven:3.8.6-amazoncorretto-17 AS builder

ADD ./pom.xml pom.xml
ADD ./src src/

RUN mvn clean package

FROM amazoncorretto:17.0.5

COPY --from=builder target/memorylane-1.0-SNAPSHOT.jar memorylane-1.0-SNAPSHOT.jar
CMD ["java", "-cp", "memorylane-1.0-SNAPSHOT.jar", "me.siquel.Main"]
