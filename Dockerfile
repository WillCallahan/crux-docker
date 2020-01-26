FROM clojure:openjdk-8-lein

COPY . /app

ENV LOG_DIR /app/logs

EXPOSE 3000/tcp

WORKDIR /app

ENTRYPOINT ["java"]

CMD ["-jar", "crux-docker-standalone.jar"]