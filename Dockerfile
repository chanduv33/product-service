FROM azul/zulu-openjdk-alpine:11.0.4
COPY target/product-service-0.0.1-SNAPSHOT.jar product-service.jar
ENTRYPOINT ["java","-jar","product-service.jar"]