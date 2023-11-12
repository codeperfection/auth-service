FROM gradle:8.4-jdk21 as build
COPY . /app
RUN addgroup authservice \
    && adduser --system --disabled-password --no-create-home --shell /bin/false --home /app --ingroup authservice authservice \
    && chown -R authservice:authservice /app
USER authservice
WORKDIR /app
RUN gradle clean build

FROM amazoncorretto:21-alpine-jdk
RUN addgroup authservice \
    && adduser --system --disabled-password --shell /bin/false --home /app --ingroup authservice authservice
USER authservice
COPY --from=build /app/build/libs/auth-service-*.jar /app/app.jar
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "/app/app.jar"]
