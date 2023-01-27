FROM maven:3.8 AS MAVEN_BUILD
ARG GOOGLE_MAPS_API_KEY
COPY ./ ./

RUN mvn --quiet clean package -DskipTests -DGOOGLE_MAPS_API_KEY=$GOOGLE_MAPS_API_KEY

FROM openjdk:17

COPY --from=MAVEN_BUILD /goods-partner-backend/target/goods-partner-backend-1.0-SNAPSHOT.jar /goods-partner-backend-1.0-SNAPSHOT.jar

CMD ["java", "-jar", "goods-partner-backend-1.0-SNAPSHOT.jar"]