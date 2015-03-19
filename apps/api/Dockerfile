FROM dockerfile/java:oracle-java8
MAINTAINER Josiah Gore <josiahgore@gmail.com>

ADD build/libs/subrosa-api-1.0.0-SNAPSHOT.jar /subrosa-api.jar

RUN useradd -u 1000 -m -s /bin/bash subrosa
EXPOSE 8080
USER subrosa

ENTRYPOINT ["java", "-jar", "/subrosa-api.jar"]
