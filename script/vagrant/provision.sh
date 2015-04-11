#!/bin/bash

uname -a
uname -r

wget -q -O - https://get.docker.io/gpg | apt-key add -
echo deb http://get.docker.io/ubuntu docker main > /etc/apt/sources.list.d/docker.list
apt-get update -qq
apt-get install -q -y linux-image-generic-lts-trusty
apt-get install -q -y linux-headers-$(uname -r)
apt-get install -q -y build-essential dkms

curl -s -L https://github.com/docker/compose/releases/download/1.1.0/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

cp /tmp/docker /etc/default/docker
sudo service docker restart

cat <<'EOF' > /home/vagrant/.bashrc
export DOCKER_HOST=tcp://127.0.0.1:4243

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

