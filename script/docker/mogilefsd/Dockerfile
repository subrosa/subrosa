FROM ubuntu:trusty
MAINTAINER Josiah Gore <josiah@subrosagames.com>

RUN apt-get update
RUN apt-get upgrade -y
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y cpanminus build-essential libpq-dev

RUN cpanm install --force MogileFS::Store::Postgres
RUN cpanm install DBD::Pg
RUN cpanm install MogileFS::Utils

RUN useradd -s /bin/bash -m mogilefsd && \
    mkdir -p /etc/mogilefs && \
    touch /etc/mogilefs/mogilefsd.conf /etc/mogilefs/mogilefs.conf && \
    chown mogilefsd /etc/mogilefs/mogilefsd.conf /etc/mogilefs/mogilefs.conf

ADD run.sh /run.sh
RUN chmod 755 /run.sh

USER mogilefsd

EXPOSE 7001
CMD ["./run.sh"]
