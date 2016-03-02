#!/bin/bash

BLUE='\033[0;34m'
NC='\033[0m' # No Color

uname -a
uname -r

msg() {
  echo
  echo -e [${BLUE}SUBROSA${NC}] $1
  echo
}

msg "Looking for Java 8..."
VERSION=0
if type -p java; then
  VERSION=$(java -version 2>&1 | sed 's/java version "\(.*\)\.\(.*\)\..*"/\1\2/; 1q')
  msg "...Found java version ${VERSION}"
else
  msg "...Java not found"
fi

if [ "$VERSION" -lt 18 ]; then
  msg "Installing Oracle Java 8..."
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
    add-apt-repository -y ppa:webupd8team/java && \
    apt-get update -q && \
    apt-get install -y oracle-java8-installer && \
    rm -rf /var/lib/apt/lists/* && \
    rm -rf /var/cache/oracle-jdk8-installer
fi

msg "Installing Docker and its dependencies..."

msg "Looking for docker..."
VERSION=0
if type -p docker; then
  VERSION=$(docker --version 2>&1 | sed 's/Docker version \(.*\)\.\(.*\)\..*/\1\2/; 1q')
  msg "...Found docker version ${VERSION}"
else
  msg "...Docker not found"
fi

if [ "$VERSION" -lt 19 ]; then
  msg "Installing latest docker..."
  apt-get update
  apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
  mkdir -p /etc/apt/sources.list.d
  echo "deb https://apt.dockerproject.org/repo ubuntu-trusty main" > /etc/apt/sources.list.d/docker.list
  apt-get update
  apt-get purge -y -qq docker\*
  apt-get autoremove -y -qq
  apt-get install -y -qq docker-engine=1.8.1-0~trusty
  usermod -aG docker vagrant
fi

msg "Installing docker-compose and docker utilities..."
mkdir -p /home/vagrant/bin
wget -nv -O /home/vagrant/bin/docker-compose https://github.com/docker/compose/releases/download/1.5.2/docker-compose-Linux-x86_64
wget -nv -O /home/vagrant/bin/docker-gc https://raw.githubusercontent.com/spotify/docker-gc/master/docker-gc
wget -nv -O /home/vagrant/bin/docker-volumes https://github.com/cpuguy83/docker-volumes/releases/download/v1.1.2/docker-volumes-linux-amd64
chmod +x /home/vagrant/bin/docker-{compose,gc,volumes}

msg "Other utilties..."
apt-get install -y htop tmux linux-image-extra-virtual

msg "Restarting docker..."
cp /tmp/docker /etc/default/docker
sudo service docker restart

msg "Initializing shell configs and cleaning up..."

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

