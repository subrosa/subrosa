FROM ubuntu:trusty
MAINTAINER Josiah Gore <josiah@subrosagames.com>

RUN apt-get update
RUN apt-get upgrade -y
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y cpanminus build-essential

# force so that we don't have to set up a db for tests to pass
RUN cpanm install --force MogileFS::Server

RUN useradd -s /bin/bash -m mogstored && \
    mkdir -p /etc/mogilefs /var/mogdata/dev1
RUN chown -R mogstored /etc/mogilefs /var/mogdata

ADD mogstored.conf /etc/mogilefs/mogstored.conf
ADD run.sh /run.sh
RUN chmod 755 /run.sh

VOLUME ["/var/mogdata"]
EXPOSE 7500 7501

USER mogstored
CMD ["/run.sh"]
