FROM ubuntu:trusty
MAINTAINER Josiah Gore <josiah@subrosagames.com>

#Install PostgreSQL-9.3
RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y postgresql-9.3

# Adjust PostgreSQL configuration so that remote connections to the database are possible.
RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/9.3/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> /etc/postgresql/9.3/main/postgresql.conf

# Set default credentials
ENV PGUSER postgres
ENV PGPASSWORD postgres

ADD run.sh /subrosa/
RUN chmod 755 /subrosa/run.sh

ADD engine.sql /subrosa/
ADD populate.sh /subrosa/
RUN chmod 755 /subrosa/populate.sh

USER postgres

WORKDIR /subrosa
RUN ["./populate.sh"]

# Add VOLUMEs to allow backup of config, logs and databases
VOLUME	["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

EXPOSE 5432

CMD ["./run.sh"]
