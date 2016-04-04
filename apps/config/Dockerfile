FROM java:8
MAINTAINER Josiah Gore <josiahgore@gmail.com>

RUN useradd -u 1000 -m -s /bin/bash subrosa
USER subrosa

EXPOSE 8888

ADD build/libs/subrosa-config-1.0.0-SNAPSHOT.jar /subrosa-config.jar
ENTRYPOINT ["java", "-jar", "/subrosa-config.jar"]
