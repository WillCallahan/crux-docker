FROM clojure:openjdk-8-lein

COPY . /app

ARG JAR_FILE

ENV SERVER_PORT 3000
ENV LOG_DIR /app/logs
ENV JAR_ENTRY_POINT ${JAR_FILE}

EXPOSE ${SERVER_PORT}/tcp

WORKDIR /app

ENTRYPOINT ["java"]

CMD ["-jar", "target/crux-docker-standalone.jar"]