FROM ubuntu:trusty
MAINTAINER Josiah Gore <josiah@subrosagames.com>

RUN apt-get update
RUN apt-get upgrade -y
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y curl tar openjdk-7-jre-headless libpostgresql-jdbc-java

# Get flyway
RUN curl -O http://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/3.0/flyway-commandline-3.0.tar.gz
RUN tar -xzvf flyway-commandline-3.0.tar.gz

WORKDIR flyway-3.0

# Copy the postgres driver to its required location
RUN cp /usr/share/java/postgresql-jdbc4.jar jars/

ADD sql/ sql/

ADD run.sh ./run.sh
ENTRYPOINT ["./run.sh"]

