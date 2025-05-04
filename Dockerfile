FROM openjdk:21-slim

WORKDIR /app

COPY build.gradle gradlew settings.gradle ./
COPY gradle/ gradle/

# Install findutils which provides xargs and execute wrapper
RUN apt-get update && \
    apt-get install -y findutils && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    ./gradlew wrapper

COPY src/ src

RUN ./gradlew build

ENTRYPOINT ["java","-jar","build/libs/sleep-0.0.1-SNAPSHOT.jar"]
