FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

LABEL maintainer="xuesong.lei <228389787@qq.com>"

ENV TZ=Asia/Shanghai

RUN apt-get update && apt-get install -y --no-install-recommends tzdata \
    && ln -sf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /aegis

COPY --from=builder /build/target/aegis-1.0.0.jar /aegis/aegis-1.0.0.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "/aegis/aegis-1.0.0.jar"]
