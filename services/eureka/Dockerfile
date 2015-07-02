FROM java:8
MAINTAINER Josiah Gore <josiahgore@gmail.com>

RUN useradd -u 1000 -m -s /bin/bash subrosa

USER subrosa
EXPOSE 8761

ADD build/libs/subrosa-eureka-1.0.0-SNAPSHOT.jar /subrosa-eureka.jar
ENTRYPOINT ["java", "-jar", "/subrosa-eureka.jar"]
