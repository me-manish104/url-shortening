FROM java:8
ADD ./build/libs /libs
WORKDIR /libs
EXPOSE 8050
CMD ["java","-jar","url_shortener-0.0.1-SNAPSHOT.jar"]
