#!/bin/bash

uname -a
uname -r

wget -q -O - https://get.docker.io/gpg | apt-key add -
echo deb http://get.docker.io/ubuntu docker main > /etc/apt/sources.list.d/docker.list
apt-get update -qq
apt-get install -q -y --force-yes lxc-docker
apt-get install -q -y lxc
apt-get install -q -y linux-image-generic-lts-trusty
apt-get install -q -y linux-headers-$(uname -r)
apt-get install -q -y build-essential dkms

curl -s -L https://github.com/docker/fig/releases/download/1.0.1/fig-$(uname -s)-$(uname -m) > /tmp/fig
sudo mv /tmp/fig /usr/local/bin/fig
sudo chmod +x /usr/local/bin/fig

cp /tmp/docker /etc/default/docker
sudo service docker restart

cat <<'EOF' > /home/vagrant/.bashrc
export DOCKER_HOST=tcp://127.0.0.1:4243

# load up environment variables
source /vagrant/script/vagrant/default.env
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

