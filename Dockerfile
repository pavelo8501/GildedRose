FROM eclipse-temurin:23-jre

WORKDIR /app

COPY /build/libs/ /app/

ENTRYPOINT ["java", "-jar", "/app/gildedrose.jar"]