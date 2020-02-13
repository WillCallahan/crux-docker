FROM openjdk:8-alpine

COPY . /app

EXPOSE 3000/tcp

WORKDIR /app

ENTRYPOINT ["java"]

CMD ["-jar", "crux-docker-standalone.jar"]