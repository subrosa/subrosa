#!/bin/bash

BLUE='\033[0;34m'
NC='\033[0m' # No Color

uname -a
uname -r

echo
echo -e [${BLUE}SUBROSA${NC}] Installing Oracle Java 8...
echo

echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update -q && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer

echo
echo -e [${BLUE}SUBROSA${NC}] Installing Docker and its dependencies...
echo

wget -q -O - https://get.docker.io/gpg | apt-key add -
echo deb http://get.docker.io/ubuntu docker main > /etc/apt/sources.list.d/docker.list

apt-get update
apt-get install -q -y linux-image-generic-lts-trusty
apt-get install -q -y linux-headers-$(uname -r)
apt-get install -q -y build-essential dkms
apt-get install -q -y lxc lxc-docker

echo
echo -e [${BLUE}SUBROSA${NC}] Installing docker-compose...
echo

curl -s -L https://github.com/docker/compose/releases/download/1.1.0/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

cp /tmp/docker /etc/default/docker
sudo service docker restart

echo
echo -e [${BLUE}SUBROSA${NC}] Initializing shell configs and cleaning up...
echo

cat <<'EOF' > /home/vagrant/.bashrc
export DOCKER_HOST=tcp://127.0.0.1:4243
export JAVA_HOME=/usr/lib/jvm/java-8-oracle

# load up environment variables
source /vagrant/script/vagrant/default.env
if [ -f /vagrant/script/vagrant/generated.env ]; then
    source /vagrant/script/vagrant/generated.env
fi
if [ -f /vagrant/script/vagrant/local.env ]; then
    source /vagrant/script/vagrant/local.env
fi

# load up shell configs
source /vagrant/script/vagrant/bashrc
if [ -f /vagrant/script/vagrant/local.bashrc ]; then
    source /vagrant/script/vagrant/local.bashrc
fi

# jump to source tree
cd /vagrant
EOF

