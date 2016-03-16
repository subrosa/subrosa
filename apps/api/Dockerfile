FROM java:8
MAINTAINER Josiah Gore <josiahgore@gmail.com>

RUN useradd -u 1000 -m -s /bin/bash subrosa
USER subrosa

EXPOSE 8080

ADD build/libs/subrosa-api-1.0.0-SNAPSHOT.jar /subrosa-api.jar
ENTRYPOINT ["java", "-jar", "/subrosa-api.jar"]
