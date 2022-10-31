# Adapted from https://blog.wolt.com/engineering/2022/05/13/how-to-reduce-jvm-docker-image-size/
FROM amazoncorretto:17.0.5 as corretto-jdk

# required for objcopy
RUN yum install -y binutils

# Build small JRE image
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /customjre

FROM gradle:7.5.1-jdk17 as builder

COPY --chown=gradle:gradle ./build.gradle.kts ./settings.gradle.kts ./memorylane/
COPY --chown=gradle:gradle ./src ./memorylane/src
WORKDIR /home/gradle/memorylane

RUN gradle fatJar

FROM amazonlinux:2

ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Configure working directory
RUN mkdir /app && \
    chown -R 1000:1000 /app

# copy JRE from the base image
COPY --from=corretto-jdk /customjre $JAVA_HOME
COPY --from=builder --chown=1000:1000 /home/gradle/memorylane/build/libs/memorylane-1.0-SNAPSHOT.jar /app/memorylane-1.0-SNAPSHOT.jar

WORKDIR /app

CMD ["/jre/bin/java", "-cp", "/app/memorylane-1.0-SNAPSHOT.jar", "me.siquel.Main"]
