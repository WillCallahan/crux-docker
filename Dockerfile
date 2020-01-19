FROM clojure:openjdk-14-lein-alpine

COPY . /app

ARG JAR_FILE

ENV SERVER_PORT 3000
ENV LOG_DIR /app/logs

EXPOSE ${SERVER_PORT}/tcp

ENTRYPOINT ["java"]
CMD ["-jar", "${JAR_FILE}"]