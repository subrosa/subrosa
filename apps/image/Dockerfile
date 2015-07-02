FROM java:8
MAINTAINER Josiah Gore <josiahgore@gmail.com>

ADD build/libs/subrosa-image-1.0.0-SNAPSHOT.jar /subrosa-image.jar

RUN useradd -u 1000 -m -s /bin/bash subrosa
EXPOSE 8080
USER subrosa

ENTRYPOINT ["java", "-jar", "/subrosa-image.jar"]
