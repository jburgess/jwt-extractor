FROM oracle/graalvm-ce:19.3.3-java11 AS builder
LABEL maintainer="joel.burgess@gmail.com>"
ENV GRADLE_HOME=/opt/gradle/gradle-6.6
ENV PATH="/opt/gradle/gradle-6.6/bin:${PATH}"

RUN yum install -y unzip wget
RUN wget https://services.gradle.org/distributions/gradle-6.6-bin.zip \
    && unzip -d /opt/gradle gradle-*.zip \
    && rm -rf gradle-*.zip

COPY build.gradle settings.gradle /
COPY src /src

RUN gradle build nativeImage
RUN chmod +x build/bin/jwt-extractor

#FROM oraclelinux:7-slim
#FROM alpine:3.9.4
FROM scratch
# Copy our static executable.
COPY --from=builder build/bin/jwt-extractor .
#COPY build/bin/jwt-extractor /usr/bin
#RUN chmod +x /usr/bin/jwt-extractor
EXPOSE 8080
CMD ["./jwt-extractor"]
