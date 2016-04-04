FROM java:8
MAINTAINER Josiah Gore <josiahgore@gmail.com>

RUN useradd -u 1000 -m -s /bin/bash subrosa
USER subrosa

EXPOSE 8080

ADD build/libs/subrosa-image-1.0.0-SNAPSHOT.jar /subrosa-image.jar
ENTRYPOINT ["java", "-jar", "/subrosa-image.jar"]
