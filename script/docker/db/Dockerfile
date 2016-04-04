FROM postgres:9.5.1
MAINTAINER Josiah Gore <josiah@subrosagames.com>

# Set default credentials
ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD postgres

# Add VOLUMEs to allow backup of config, logs and databases
VOLUME	["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

ADD populate.sh /docker-entrypoint-initdb.d/
ADD engine.sql /
