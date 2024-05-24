#Dockerfile
# First stage: SBT build
FROM mozilla/sbt as builder
COPY . /app
WORKDIR /app
RUN sbt clean compile
RUN sbt assembly


# Fetch base image
FROM openjdk:17-jdk-slim
COPY --from=builder /app/target/scala-2.13/app-assembly-0.1.0-SNAPSHOT.jar /myapp.jar
#COPY ./target/scala-2.13/helloworld-assembly-0.1.0-SNAPSHOT.jar /myapp.jar
CMD java -jar myapp.jar
