FROM amazoncorretto:22-alpine3.20 as build
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
WORKDIR /app
COPY . .
RUN apk add maven
RUN mvn clean package -DskipTests=true
RUN apk add binutils
RUN $JAVA_HOME/bin/jlink \
--add-modules java.base,java.desktop,java.naming,java.xml,java.management,java.sql,java.instrument,java.management,java.rmi,java.scripting,java.security.jgss \
--strip-debug \
--no-man-pages \
--no-header-files \
--compress=2 \
--output /javaruntime
FROM alpine:3.20
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=build /javaruntime $JAVA_HOME
WORKDIR /app
COPY --from=build /app/target/webflux-payground-0.0.1-SNAPSHOT.jar /app/webflux-payground.jar
#EXPOSE 8080
#ENTRYPOINT exec java $JAVA_OPTS -jar elasticapp.jar
#WORKDIR /app/build/libs
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar webflux-payground.jar
#CMD ["./reactor"]